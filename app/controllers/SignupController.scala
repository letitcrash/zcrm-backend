package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}
import database.SignupRepository 

import play.api.Logger
import scala.util.{Success, Failure, Try}


@Singleton
class SignupController @Inject()  extends CRMController {

  def token = CRMAction { rq =>
    import utils.JSFormat._
    implicit val validationFrmt = Json.format[SignupToken]

    Logger.info(" SignupController is runningn ... ")
   
    val token : Try[SignupToken] =  SignupRepository.createTokenForEmail("ievgen.paliichuk@gmail.com")
    Json.toJson(token.get)

  }


}


