package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.MailboxDBRepository
import play.api.libs.json.Json
import scala.concurrent.Future

@Singleton
class MailboxController @Inject() extends CRMController {
  import utils.JSFormat.mailboxFrmt

	def newMailbox(userId: Int) = CRMActionAsync[Mailbox](expectedMailboxFormat){rq =>
		MailboxDBRepository.saveMailbox(rq.body).map(res => Json.toJson(res))
	}

	def getMailboxById(userId: Int, mailboxId: Int) = CRMActionAsync{rq =>
		MailboxDBRepository.getMailboxById(mailboxId).map(res => Json.toJson(res))
	}

	def getAllMailboxesByUserId(userId: Int) = CRMActionAsync{rq =>
		MailboxDBRepository.getAllMailboxesByUserId(userId).map(list => Json.toJson(list))
	}

	def updateMailBox(userId: Int, mailboxId: Int) = CRMActionAsync[Mailbox](expectedMailboxFormat){rq =>
		MailboxDBRepository.updateMailbox(rq.body).map(res => Json.toJson(res))
	}

	def softDeleteMailbox(userId: Int, mailboxId: Int) = CRMActionAsync{rq =>
		MailboxDBRepository.softDeleteMailboxById(mailboxId).map(res => Json.toJson(res))
	}
 
}
