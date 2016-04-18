package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import utils.ews._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{EmployeeDBRepository,UserDBRepository, MailboxDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import collection.JavaConversions._

@Singleton
class ExchangeController @Inject() (ewsAuth: EwsAuthUtil, ewsMail: EwsMailUtil) extends CRMController {
  import utils.JSFormat.inboxMailFrmt
  import utils.JSFormat.outboxMailFrmt

   val DEFAULT_EXCHANGE_SERVER = "https://sd-74609.multimedianordic.no/EWS/Exchange.asmx";
  //TODO: add pagination 
  def getInboxEmails(userId: Int, mailboxId: Int) = CRMActionAsync { rq =>
    import utils.converters.MailConverter._
   // if(rq.header.belongsToCompany(companyId)){
			MailboxDBRepository.getMailboxById(mailboxId).flatMap{mailBox =>	
      		val ewsService = ewsAuth.checkUserLogin(mailBox.server, mailBox.login, mailBox.password)
      		val mailsArray: Array[EwsInboxMail] = ewsMail.getInboxMail(ewsService, 1, 20)
      		val mailList = mailsArray.toList.map(_.asInboxMail)
          Future(Json.toJson(mailList)) 
				}
    //}else{ Failure(new InsufficientRightsException()) }
  }

  //TODO: add pagination 
  def getOutboxEmails(userId: Int, mailboxId: Int) = CRMActionAsync  { rq =>
    import utils.converters.MailConverter._
    // if(rq.header.belongsToCompany(companyId)){
			MailboxDBRepository.getMailboxById(mailboxId).flatMap{mailBox =>	
      		val ewsService = ewsAuth.checkUserLogin(mailBox.server, mailBox.login, mailBox.password)
      		val mailsArray: Array[EwsSentMail] = ewsMail.getSentMail(ewsService, 1, 20)      
					val mailList = mailsArray.toList.map(_.asOutboxMail)
          Future(Json.toJson(mailList)) 
				}
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
  
  def sendEmail(userId: Int, mailboxId: Int) = CRMActionAsync[MailToSend](expectedMailToSendFormat) { rq =>
 			MailboxDBRepository.getMailboxById(mailboxId).flatMap{mailBox =>	
      		val ewsService = ewsAuth.checkUserLogin(mailBox.server, mailBox.login, mailBox.password)
      		ewsMail.sendMail(ewsService, rq.body.asEwsMailToSend)
          //TODO: add JSON response 
    			Future(Json.toJson("mail sent")) 
				}
  }
	
	case class extMailId(extMailId: String)
	implicit val expectedExtMailIdFrmt = Json.format[extMailId]	
	def getMail(userId: Int, mailboxId: Int) = CRMActionAsync[extMailId](expectedExtMailIdFormat){ rq =>
    import utils.converters.MailConverter._
 			MailboxDBRepository.getMailboxById(mailboxId).flatMap{mailBox =>	
      		val ewsService = ewsAuth.checkUserLogin(mailBox.server, mailBox.login, mailBox.password)
      		val mail = ewsMail.getMailById(ewsService, rq.body.extMailId)
					Future(Json.toJson(mail.asInboxMail))
				}
	}

}
