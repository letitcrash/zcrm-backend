package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import utils.ExpectedFormat._

@Singleton
class EmployeeController @Inject() (mailer: utils.Mailer) extends CRMController {

}
