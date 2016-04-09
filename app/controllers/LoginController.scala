package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import controllers.session.{CRMResponseHeader, CRMResponse}
import database.{EmployeeDBRepository, UserDBRepository}
import models._
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import security.Security
import scala.util.{Try, Success, Failure}

case class LoginRequest(
  username: String,
  password: String)

case class LogonResponse(
  user: User,
  employee: Employee,
  sessionToken: Option[String] = None)

@Singleton
class LoginController @Inject() (mailer: utils.Mailer) extends CRMController {
  import utils.JSFormat.{userFrmt, responseFrmt, employeeFrmt}
  implicit val requestFormat = Json.format[LoginRequest]
  implicit val responseFormat = Json.format[LogonResponse]

  private val expectedLoginRq = Json.toJson(Map(
    "username" -> "[M](string) Username",
    "password"   -> "[M](string) Password"))

  def login = Action.async (parse.json) { rq =>
    rq.body.validate[LoginRequest] map { body =>
      UserDBRepository.loginUser(body.username, body.password)
        .flatMap( user => EmployeeDBRepository.getEmployeesByUser(user).map(
          employee => createResponse(user, employee) match {
                        case Success(res) => Ok(Json.toJson(res))
                        case Failure(ex)  => Ok(Json.toJson(createFailedResponse(ex)))
        }))
        .recover{ case ex => Ok(Json.toJson(createFailedResponse(ex))) }
    } recoverTotal( e => Future { BadRequest(Json.toJson(Map(
                         "expected"  -> expectedLoginRq,
                         "error"     -> Json.toJson(JsError.toFlatJson(e)))))})
  }
  
  private[LoginController] def createResponse(user: User, employee: Employee): Try[CRMResponse] = {
    for {
      token <- Security.createSessionToken(user, employee)
    } yield CRMResponse(CRMResponseHeader(),
                       Option(Json.toJson(LogonResponse(user = user,
                                                        employee = employee,
                                                        sessionToken = Some(token)))))
  }

  private[LoginController] def createFailedResponse(ex: Throwable): CRMResponse = {
    CRMResponse(CRMResponseHeader(-1), None)
  }

}
