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
  import utils.JSFormat.mailFrmt

  def getAllEmails(companyId: Int, employeeId: Int) = CRMAction  { rq =>
    import utils.converters.MailConverter._
   // if(rq.header.belongsToCompany(companyId)){
      val ewsService = ewsAuth.checkUserLogin("Administrateur@multimedianordic.no", "Stein4201")
      val mailsArray: Array[MessageToReceive] = ewsMail.getInboxMail(ewsService, 1, 1)
      val mailList = mailsArray.toList.map(_.asMail)
      Json.toJson(mailList) 
    //}else{ Failure(new InsufficientRightsException()) }
  }

}