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
  import utils.JSFormat.contactProfileFrmt

  case class InviteEmployee(username: String,
                            baseUrl: String,
                            contactProfile: ContactProfile,
                            employeeLevel: Option[Int],
                            employeeType: Option[String]) {

                              def toEmployee(companyId: Int) = {
                                Employee(
                                  companyId = companyId,
                                  //employeeType = employeeType,
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

  def softDeleteEmployee(companyId: Int, employeeId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.softDeleteEmployeeById(employeeId).map(deletedEmpl => Json.toJson(deletedEmpl))
  }

  def getAllEmployeesByCompanyId(companyId: Int) = CRMActionAsync{rq =>
    EmployeeDBRepository.getEmployeesByCompanyId(companyId).map(list => Json.toJson(list))
  }
}
