package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}
import database.SignupRepository 
import database.UserDBRepository

import play.api.Logger
import scala.util.{Success, Failure, Try}
import scala.concurrent.{Await, Future} 
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration.Duration

import controllers.session.{CRMResponseHeader, CRMResponse}
import play.api.libs.json.{JsError, Reads, JsValue, Json, JsSuccess}


@Singleton
class SignupController @Inject() (mailer: utils.Mailer) extends CRMController {

 private implicit val sendEmailRdr = Json.reads[SendEmailRq]


  private val errEmailAlreadyExist = 502
  private val errTokenAlreadyExists = 601

  case class SendEmailRq(
    email: String,
    url: String)

 import utils.JSFormat.responseFrmt

  private val tokenExistErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("A token for that email already exists"),
      response_code = errTokenAlreadyExists),
    None))


  private val mailAlreadyExist = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("A user with the email already exist"),
      response_code = errEmailAlreadyExist),
    None
  ))

  private def signupMailSentResponse(email: String): JsValue = {
    val body = Json.toJson(Map("mailed" -> email))
    Json.toJson(CRMResponse(CRMResponseHeader(), Some(body)))
  }

  def sendSignupEmail = Action.async (parse.json) { rq =>

       rq.body.validate[SendEmailRq] match {
                            case s: JsSuccess[SendEmailRq] => 
                              if (validEmail(s.get.email)) {
                                 val userF =  UserDBRepository.getUserByUsername(s.get.email)
                                 val tokenF = SignupRepository.findUsableToken(s.get.email) 
                                 userF.map(user =>  Ok(mailAlreadyExist) ).recoverWith {
                                   case ex =>
                                     tokenF.map(token => Ok(tokenExistErr)).recover{
                                       case ex =>
                                          SignupRepository.createTokenForEmail(s.get.email) match {
                                            case Success(token) =>
                                              mailer.sendSignupEmail(s.get.email, token.token, s.get.url) match {
                                                 case Success(_) => Ok(signupMailSentResponse(s.get.email))
                                                 case Failure(ex) =>ex.toResp
                                              }
                                            case Failure(ex) => ex.toResp
                                          }
                                     }
                                 } 
                              }else {
                                Future{ BadRequest( Json.toJson(Map("result" -> "-1",
                                                                    "message" -> "Invalid email format",
                                                                    "reason" -> s.get.email)))}
                              }
                            case e: JsError => Future { BadRequest(jsonError("Invalid format", e)) }
    }

  }

  def validEmail(str: String): Boolean = {
    str.contains("@")
  }

///DELETE BELOW

/*
  def token = CRMAction { rq =>
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    val token : Try[SignupToken] =  SignupRepository.createTokenForEmail("ievgen.paliichuk@gmail.com")
    Json.toJson(token.get)

  }

  def getUser = CRMActionAsync { implicit rq => 
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    SignupRepository.findToken("ef4ih074ul35886hrkci8b0cc963ddn1s486apgecb6d")
        .map(result => Json.toJson(result))
  }

  def findTokenAsync = CRMActionAsync { implicit rq => 
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]
    SignupRepository.findToken("ef4ih074ul35886hrkci8b0cc963ddn1s486apgecb6d")
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
    SignupRepository.findToken("ef4ih074ul35886hrkci8b0cc963ddn1s486apgecb6d") .map(result => Json.toJson(result))

  }

  */
}

case class Valid(
  async: String, 
  test: String 
)

