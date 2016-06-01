package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{EmployeeDBRepository,UserDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import utils.JSFormat.employeeFrmt

@Singleton
class EmployeeController @Inject() (mailer: utils.Mailer) extends CRMController {
  import utils.JSFormat._

  case class InviteEmployee(username: String,
                            baseUrl: String,
                            contactProfile: ContactProfile,
                            position: Option[Position] = None, 
                            shift: Option[Shift] = None,
                            department: Option[Department] = None, 
                            union: Option[Union] = None, 
                            teams: Option[List[Team]] = None,
                            delegates: Option[List[Delegate]] = None,
                            employeeLevel: Option[Int]) {

                              def toEmployee(companyId: Int) = {
                                Employee(
                                  companyId = companyId,
                                  position = position,
                                  shift = shift, 
                                  department = department,
                                  union = union, 
                                  teams = teams,
                                  delegates = delegates, 
                                  employeeLevel = employeeLevel.getOrElse(UserLevels.USER)
                                )
                              }

                            }


  implicit val inviteEmployeeFrmt = Json.format[InviteEmployee]

  def inviteEmployee(companyId: Int) = CRMActionAsync[InviteEmployee](expectedInviteEmployeeFormat) { rq =>
    import utils.JSFormat.employeeFrmt
    if(rq.header.isCompanyManager || rq.header.isCompanyOwner){
      //TODO: should be transactionally
      EmployeeDBRepository.createEmployee( username = rq.body.username,
                                           contactProfile = rq.body.contactProfile,
                                           employee = rq.body.toEmployee(companyId)).flatMap( employee => 
                                             UserDBRepository.createPasswordToken(employee.user.get).map( token =>
                                                 mailer.sendSetPasswordLink(token, rq.body.baseUrl, employee.user.get)
                                                   .map( unit => Json.toJson(employee))))
    }else{ Future{Failure(new InsufficientRightsException())} }
  }

  def getEmployee(companyId: Int, employeeId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.getEmployeeByEmployeeId(employeeId).map(empl => Json.toJson(empl))
  }

  def updateEmployee(companyId: Int, employeeId: Int) = CRMActionAsync[Employee](expectedEmployeeFormat){rq =>
    EmployeeDBRepository.updateEmployee(rq.body).map(updated => Json.toJson(updated))     
  }

  def updateEmployeeContactProfile(companyId: Int, employeeId: Int) = CRMActionAsync[ContactProfile](expectedContactProfileFormat){rq =>
    EmployeeDBRepository.updateEmployeeContactProfile(rq.body).map(updated => Json.toJson(updated))     
  }

  def softDeleteEmployee(companyId: Int, employeeId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.softDeleteEmployeeById(employeeId).map(deletedEmpl => Json.toJson(deletedEmpl))
  }


  /*
  def getAllEmployeesByCompanyId(companyId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.getAggragatedEmployeesByCompanyId(companyId).map(list => Json.toJson(list))
  }
  */

  def searchAllEmployeesByCompanyId(companyId: Int,
                                    positionIds: List[Int],
                                    pageSize: Option[Int], 
                                    pageNr: Option[Int],
                                    searchTerm: Option[String]) = CRMActionAsync{rq =>

    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      EmployeeDBRepository.searchAggragatedEmployeesByCompanyId(companyId,
                                                                positionIds,
                                                                psize,
                                                                pnr,
                                                                searchTerm).map(page => Json.toJson(page))
    } else {
      EmployeeDBRepository.getAggragatedEmployeesByCompanyId(companyId, positionIds).map(list => 
        Json.toJson( PagedResult[Employee]( pageSize = list.length,
                                           pageNr = 1,
                                           totalCount = list.length,
                                           data = list)))
    }
  }

  def updateEmployeePositionById(companyId: Int,  employeeId: Int, positionId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.updateEmployeePosition(employeeId, positionId).map(empl => Json.toJson(empl))
  }

  def clearEmployeePositionById(companyId: Int,  employeeId: Int, positionId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.clearEmployeePosition(employeeId, positionId).map(empl => Json.toJson(empl))
  }

  def updateEmployeeShiftById(companyId: Int, employeeId: Int, shiftId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.updateEmployeeShift(employeeId, shiftId).map(empl => Json.toJson(empl))
  }

  def clearEmployeeShiftById(companyId: Int, employeeId: Int, shiftId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.clearEmployeeShift(employeeId, shiftId).map(empl => Json.toJson(empl))
  }

  def updateEmployeeDepartmentById(companyId: Int, employeeId: Int, departmentId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.updateEmployeeDepartment(employeeId, departmentId).map(empl => Json.toJson(empl))
  }

  def clearEmployeeDepartmentById(companyId: Int, employeeId: Int, departmentId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.clearEmployeeDepartment(employeeId, departmentId).map(empl => Json.toJson(empl))
  }

  def updateEmployeeUnionById(companyId: Int, employeeId: Int, unionId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.updateEmployeeUnion(employeeId, unionId).map(empl => Json.toJson(empl))
  }

  def clearEmployeeUnionById(companyId: Int, employeeId: Int, unionId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.clearEmployeeUnion(employeeId, unionId).map(empl => Json.toJson(empl))
  }

  
  def updateEmployeeDelegatesById(companyId: Int, employeeId: Int) = CRMActionAsync[Delegates](expectedDelegateFormat){rq =>
    EmployeeDBRepository.updateEmployeeDelegate(employeeId, rq.body.delegates).map(empl => Json.toJson(empl))
  }

  def clearEmployeeDelegatesById(companyId: Int, employeeId: Int) = CRMActionAsync{ rq => 
    EmployeeDBRepository.clearEmployeeDelegate(employeeId).map(empl => Json.toJson(empl))
  }


  def updateEmployeeTeamsById(companyId: Int, employeeId: Int) = CRMActionAsync[Teams](expectedTeamFormat){rq =>
    EmployeeDBRepository.updateEmployeeTeam(employeeId, rq.body.teams).map(empl => Json.toJson(empl))
  }

  def clearEmployeeTeamsById(companyId: Int, employeeId: Int) = CRMActionAsync{ rq => 
    EmployeeDBRepository.clearEmployeeTeam(employeeId).map(empl => Json.toJson(empl))
  }

}
