package controllers

import scala.concurrent.Future
import scala.util.control.NonFatal

import database.ExchangeRepository
import database.MailboxDBRepository
import database_rf.Database
import database_rf.MailboxesDBComponent
import javax.inject.Inject
import javax.inject.Singleton
import models.Error
import models.Mailbox
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import utils.ExpectedFormat.expectedMailboxFormat
import utils.ews.EwsAuthUtil

@Singleton
class MailboxController @Inject() (ewsAuth: EwsAuthUtil, exchange: ExchangeRepository, db: Database) extends CRMController {
  import utils.JSFormat._

  val mailboxes = MailboxesDBComponent(db)
  
  def synchronize(userId: Int) = CRMActionAsync { _ => 
    mailboxes.get(userId).map { seq =>
      if (seq.length > 0) {
        Json.toJson(seq.map(mailboxes.synchronize(_)))
      } else {
        throw new RuntimeException("Wrong id?")
      }
    }
  }

  def newMailbox(userId: Int) = CRMActionAsync[Mailbox](expectedMailboxFormat) { rq =>
    try {
      ewsAuth.tryToLogin(rq.body.server, rq.body.login, rq.body.password)
      MailboxDBRepository.saveMailbox(rq.body, userId).map(res => Json.toJson(res))
    } catch {
      case NonFatal(e) => Future(Json.toJson(Map(
          "result" -> "-1233",
          "message" -> "Incorrect login/password",
          "reason" -> e.getMessage)))
    }
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

  def searchAllMailboxesByName(userId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      MailboxDBRepository.searchMailboxByName(userId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { MailboxDBRepository.getAllMailboxesByUserId(userId).map( mailboxes => Json.toJson(mailboxes)) }
  }
 
}
