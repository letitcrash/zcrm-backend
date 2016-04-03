package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}
import database.SignupRepository 

import play.api.Logger
import scala.util.{Success, Failure, Try}

import controllers.session.{CRMResponseHeader, CRMResponse}


@Singleton
class SignupController @Inject()  extends CRMController {

 private implicit val sendEmailRdr = Json.reads[SendEmailRq]


  private val errEmailAlreadyExist = 502

  case class SendEmailRq(
    email: String,
    url: String)

 import utils.JSFormat.responseFrmt


  private val mailAlreadyExist = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("A user with the email already exist"),
      response_code = errEmailAlreadyExist),
    None
  ))


  /*
  def sendSignupEmail = Action.async (parse.json) { rq =>

    rq.body.validate[SendEmailRq] map { body =>

      if (validEmail(body.email)) {
        if (UserDBRepository.getUserByUsername(body.email)(rq.dbSession).isSuccess)
          Ok(mailAlreadyExist)
      }

    }

  }
  */

  def validEmail(str: String): Boolean = {
    str.contains("@")
  }



  def token = CRMAction { rq =>
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    val token : Try[SignupToken] =  SignupRepository.createTokenForEmail("ievgen.paliichuk@gmail.com")
    Json.toJson(token.get)

  }

  def findTokenAsync = CRMActionAsync { implicit rq => 
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    SignupRepository.findToken("438hpq5dvilk5qatsdv9gu1dsrpnl7tdk70us21idjco")
        .map(result => Json.toJson(result))

  }

  val expectedValidationFormat = Json.toJson(Map(
    "async"    -> Json.toJson("[M](string) foo string"),
    "test"   -> Json.toJson("[M](string) bar string")
  ))

  implicit val validationFrmt = Json.format[Valid]

 
  def tokenAsync = CRMActionAsync[Valid](expectedValidationFormat) { implicit rq => 
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    SignupRepository.markTokenUsed("438hpq5dvilk5qatsdv9gu1dsrpnl7tdk70us21idjco").map(result => Json.toJson(result))
    SignupRepository.findToken("438hpq5dvilk5qatsdv9gu1dsrpnl7tdk70us21idjco") .map(result => Json.toJson(result))
    SignupRepository.findUsableToken("ievgen.paliichuk@gmail.com") .map(result => Json.toJson(result))


  }

}

case class Valid(
  async: String, 
  test: String 
)

