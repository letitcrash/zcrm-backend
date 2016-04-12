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
import controllers.session.{CRMResponseHeader, CRMResponse}
import exceptions._

@Singleton
class UserController @Inject()  extends CRMController {
  import utils.JSFormat.userFrmt

  case class SetPasswordUsingTokenBody(password: String,
                                       token: String)
  implicit val setPasswordUsingTokenRdr = Json.reads[SetPasswordUsingTokenBody]
  
  def setPasswordUsingToken(userId: Int) = Action.async(parse.json) { rq =>
    import utils.JSFormat.responseHeaderFrmt
    import utils.JSFormat.responseFrmt
    import utils.TimeFormat.timestampToString
    rq.body.validate[SetPasswordUsingTokenBody] map { body =>
      UserDBRepository.setPasswordUsingToken(userId, body.token, body.password).map( user =>
          Ok(Json.toJson(CRMResponse(CRMResponseHeader(), Some(Json.toJson(user)))))).recover{
            case ex: ExpiredTokenException =>
              Ok(Json.toJson(CRMResponseHeader(response_code = -23,
                                          error_message = Some("Token has expired"))))
            case ex: TokenAlreadyUsedException => 
              Ok(Json.toJson(CRMResponseHeader(response_code = -24,
                                          error_message = Some("Token used at: " + timestampToString(ex.usedAt)))))
            case ex => 
              Ok(Json.toJson(CRMResponseHeader(response_code = -25,
                                          error_message = Some("Invalid token"))))
      }
    } recoverTotal(e => Future { BadRequest(expectedSetPasswordUsingToken) })
  }

}
