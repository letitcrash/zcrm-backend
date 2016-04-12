package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import utils.ews._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{EmployeeDBRepository,UserDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class ExchangeController @Inject() (ewsAuth: EwsAuthUtil, ewsMail: EwsMailUtil) extends CRMController {

}
