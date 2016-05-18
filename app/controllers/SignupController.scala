package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}
import database.{SignupRepository, UserDBRepository, CompanyDBRepository, EmployeeDBRepository}

import play.api.Logger
import scala.util.{Success, Failure, Try}
import scala.concurrent.{Await, Future} 
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration.Duration

import controllers.session.{CRMResponseHeader, CRMResponse}
import play.api.libs.json.{JsError, Reads, JsValue, Json, JsSuccess}
import utils.ExpectedFormat._


@Singleton
class SignupController @Inject() (mailer: utils.Mailer) extends CRMController {

  import utils.JSFormat.contactProfileFrmt
  private implicit val activateUserRdr = Json.reads[ActivateUserRq]
  private implicit val sendEmailRdr = Json.reads[SendEmailRq]

  private val errInvalidEmail = 501
  private val errEmailAlreadyExist = 502
  private val errTokenAlreadyExists = 601
  private val errNoSuchToken = 602
  private val errTokenExpired = 603
  private val errTokenUsed = 604
  private val errEmailDoesNotMatch = 605
  private val errBadToken = 606


  case class SendEmailRq(
    email: String,
    url: String)

   case class ActivateUserRq(
    token: String,
    email: String,
    password: String,
    companyName: String,
    vatId: String,
    contactProfile: Option[ContactProfile])
 
  import utils.JSFormat.responseFrmt

  private val tokenExistErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("A token for that email already exists"),
      response_code = errTokenAlreadyExists),
    None))

  private val badTokenErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Invalid token"),
      response_code = errBadToken),
    None))

  private val noSuchTokenErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Token does not exist"),
      response_code = errNoSuchToken),
    None))

  private val tokenExpiredErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Token expired"),
      response_code = errTokenExpired),
    None))

  private val tokenUsedErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Token already used"),
      response_code = errTokenUsed),
    None))

  private val emailDoesNotMatchErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Email does not match the token email"),
      response_code = errEmailDoesNotMatch),
    None))

  private val invalidEmailErr = Json.toJson(CRMResponse(
    CRMResponseHeader(
      error_message = Some("Invalid email"),
      response_code = errInvalidEmail),
    None
  ))

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
                            case body: JsSuccess[SendEmailRq] => 
                              if (validEmail(body.get.email)) {
                                 val userF =  UserDBRepository.getUserByUsername(body.get.email)
                                 val tokenF = SignupRepository.findUsableToken(body.get.email) 
                                 userF.map(user =>  Ok(mailAlreadyExist) ).recoverWith {
                                   case ex =>
                                     tokenF.map(token => Ok(tokenExistErr)).recover{
                                       case ex =>
                                          SignupRepository.createTokenForEmail(body.get.email) match {
                                            case Success(token) =>
                                              mailer.sendSignupEmail(body.get.email, token.token, body.get.url) match {
                                                 case Success(_) => Ok(signupMailSentResponse(body.get.email))
                                                 case Failure(ex) =>ex.toResp
                                              }
                                            case Failure(ex) => ex.toResp
                                          }
                                     }
                                 } 
                              }else {
                                Future{ BadRequest( Json.toJson(Map("result" -> "-1",
                                                                    "message" -> "Invalid email format",
                                                                    "reason" -> body.get.email)))}
                              }
                            case e: JsError => Future { BadRequest(Json.toJson(Map(
                                                        "expected"  -> expectedSendEmailRqFormat,
                                                        "error"     -> Json.toJson(JsError.toJson(e)))))}
                            
    }
  }

 def activateUserWithToken = Action.async (parse.json) { rq =>

    rq.body.validate[ActivateUserRq] match {
      case body: JsSuccess[ActivateUserRq] => 
        val tokenF = SignupRepository.findToken(body.get.token)
        tokenF.flatMap( token => 
              if(!token.email.equalsIgnoreCase(body.get.email)) {Future{BadRequest(emailDoesNotMatchErr)}}
              else if(token.isExpired) {Future{BadRequest(tokenExpiredErr)}}
              else if(token.isUsed){Future{ BadRequest(tokenUsedErr)}}
              else { 
                //TODO: should be transactionally
                val tryUser = for {
                    user <- UserDBRepository.saveUser( User( username = body.get.email.toLowerCase,
                                                         contactProfile = body.get.contactProfile))
                    pwSet <- UserDBRepository.setPasswordForUser(user.id.get, body.get.password)
                    company <- CompanyDBRepository.saveCompany(Company( name = body.get.companyName,
                                                                 vatId = body.get.vatId,
                                                                 contactProfile = body.get.contactProfile))
                    emp <- EmployeeDBRepository.addEmployee(Employee( companyId = company.id.get,
                                                                      user = Some(user), 
                                                                     // employeeType = Some("Owner"),
                                                                      employeeLevel = EmployeeLevels.OWNER))
                    markUsed <- SignupRepository.markTokenUsed(token.token)
                } yield user 

                tryUser.map(user =>  Ok("activation complete\n"))
                  .recover{case ex =>  InternalServerError(ex.getMessage)}
              
              })
          .recover{case ex => BadRequest(badTokenErr)}

      case e: JsError => Future { BadRequest(Json.toJson(Map(
                                  "expected" -> expectedActivateUserRqFormat,
                                  "error" -> Json.toJson(JsError.toJson(e)))))

      }
    }
  }


  def validEmail(str: String): Boolean = {
    str.contains("@")
  }

}

