package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.EmployeeDBRepository
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class EmployeeController @Inject() (mailer: utils.Mailer) extends CRMController {
  import utils.JSFormat.contactProfileFrmt


/* 
  def post(companyId: Int) = TTDBAction[EmployeePost](expectedPostFormat) { rq =>
    import utils.JSFormat.employeeFrmt
    if(rq.body.contactProfile.email.isEmpty) Failure(new Exception("Mail must be set"))
    else {
      for {
        check <- rq.header.checkCompanyManagerOrAdmin(companyId)
        employee <- EmployeeRepository.createNewEmployee(
          username = rq.body.contactProfile.email.get,
          contactProfile = rq.body.contactProfile,
          employee = rq.body.toEmployee(companyId))(rq.dbSession)

        token <- UserDBRepository.createForgotPasswordToken(employee.user.get)(rq.dbSession)
        mailSent <- Mailer.sendSetPasswordLink(token, rq.body.baseUrl, employee.user.get)
      } yield Json.toJson(employee)
    }
  }
  */

  case class InviteEmployee(username: String,
                            baseUrl: String,
                            contactProfile: ContactProfile,
                            employeeLevel: Option[Int],
                            employeeType: Option[String]) {

                              def toEmployee(companyID: Int) = {
                                Employee(
                                  companyId = companyID,
                                  employeeType = employeeType,
                                  employeeLevel = employeeLevel.getOrElse(UserLevels.USER)
                                )
                              }

                            }


  implicit val inviteEmployeeFrmt = Json.format[InviteEmployee]
                              
  def inviteEmployee(companyId: Int) = CRMActionAsync[InviteEmployee](expectedInviteEmployeeFormat) { rq =>
    import utils.JSFormat.employeeFrmt
    if(rq.header.isCompanyOwnerOrManagerOrAdmin(companyId)){
      for{
        employee <- EmployeeDBRepository.createEmployee( username = rq.body.username,
                                                         contactProfile = rq.body.contactProfile,
                                                         employee = rq.body.toEmployee(companyId))
      } yield Json.toJson(employee)


    }else { Future{Failure(new InsufficientRightsException())} }
  }

}
