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
import collection.JavaConversions._

@Singleton
class ExchangeController @Inject() (ewsAuth: EwsAuthUtil, ewsMail: EwsMailUtil) extends CRMController {
  import utils.JSFormat.inboxMailFrmt
  import utils.JSFormat.outboxMailFrmt

  //TODO: add pagination 
  def getInboxEmails(companyId: Int, employeeId: Int) = CRMAction  { rq =>
    import utils.converters.MailConverter._
   // if(rq.header.belongsToCompany(companyId)){
      val ewsService = ewsAuth.checkUserLogin("Administrateur@multimedianordic.no", "Stein4201")
      val mailsArray: Array[EwsInboxMail] = ewsMail.getInboxMail(ewsService, 1, 20)
      val mailList = mailsArray.toList.map(_.asInboxMail)
      Json.toJson(mailList) 
    //}else{ Failure(new InsufficientRightsException()) }
  }

  //TODO: add pagination 
  def getOutboxEmails(companyId: Int, employeeId: Int) = CRMAction  { rq =>
    import utils.converters.MailConverter._
    // if(rq.header.belongsToCompany(companyId)){
    val ewsService = ewsAuth.checkUserLogin("Administrateur@multimedianordic.no", "Stein4201")
    val mailsArray: Array[EwsSentMail] = ewsMail.getSentMail(ewsService, 1, 20)
    val mailList = mailsArray.toList.map(_.asOutboxMail)
    Json.toJson(mailList)
    //}else{ Failure(new InsufficientRightsException()) }
  }

  //TODO: should be moved to models/, utils/converters/
  case class MailToSend(subject: Option[String],
                        body: Option[String],
                        to: List[String]){
                          def asEwsMailToSend: EwsMailToSend = {
                            new EwsMailToSend(
                              subject.getOrElse("No subject"),
                              body.getOrElse(""),
                              to.toArray)
                          }
                        }


  implicit val mailToSendFrmt = Json.format[MailToSend]
  
  def sendEmail(companyId: Int, employeeId: Int) = CRMAction[MailToSend](expectedMailToSendFormat) { rq => 
    val ewsService = ewsAuth.checkUserLogin("Administrateur@multimedianordic.no", "Stein4201")
    ewsMail.sendMail(ewsService, rq.body.asEwsMailToSend)
    //Json.toJson(rq.body.subject.getOrElse("")+", "+rq.body.body.getOrElse("")+", " + rq.body.to.toString)
    //TODO: add JSON response 
    Json.toJson("mail sent")
  }
	
	case class extMailId(extMailId: String)
	implicit val expectedExtMailIdFrmt = Json.format[extMailId]	
	def getMail(companyId: Int, employeeId:Int) = CRMAction[extMailId](expectedExtMailIdFormat){ rq =>
    import utils.converters.MailConverter._
		val ewsService = ewsAuth.checkUserLogin("Administrateur@multimedianordic.no", "Stein4201")
		val mail = ewsMail.getMailById(ewsService, rq.body.extMailId)
		Json.toJson(mail.asInboxMail)
	}

}
