package controllers

import play.api._
import play.api.mvc._
import models._
import utils.ExpectedFormat._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database._
import play.api.libs.json.Json
import java.sql.Timestamp

import scala.concurrent.Future

class ExchangeController extends CRMController {
  import utils.JSFormat.exchangeMailFrmt

  def getMailsForMailbox(userId: Int, mailboxId: Int) = CRMActionAsync{rq =>
    // if(rq.header.belongsToCompany(companyId)){
    ExchangeODSMailDBRepository.getODSMailsByMailboxId(mailboxId).map(mails => Json.toJson(mails))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  def getODSMailsForMailboxGrouped(userId: Int, mailboxId: Int) = CRMActionAsync{rq =>
    // if(rq.header.belongsToCompany(companyId)){
    import utils.JSFormat.groupedMailFrmt
    ExchangeODSMailDBRepository.getODSMailsByMailboxId(mailboxId).map(mails => 
          mails.groupBy(_.conversationExtId)).map(res => 
                  Json.toJson(res.map{ case (k,v) => GroupedMail(conversationId = k.get, mails = v)}))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  def getMail(userId: Int, mailboxId: Int, mailId: Int)  = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    ExchangeODSMailDBRepository.getODSMailById(mailId).map(mail => Json.toJson(mail))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  def getCalendarItemsByMailBoxId(userId:Int, mailboxId: Int, startDate: Long, endDate: Long) = CRMActionAsync{ rq =>
    import utils.JSFormat.calendarItemFrmt
    ExchangeRepository.getCalendarItemsByMailboxId(mailboxId, startDate, endDate).map(res => Json.toJson(res));
  }
}
