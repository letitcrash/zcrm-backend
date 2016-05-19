package controllers

import play.api._
import play.api.mvc._
import models._
import utils.ExpectedFormat._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database._
import play.api.libs.json.Json

import scala.concurrent.Future

class ExchangeController extends CRMController {
  import utils.JSFormat.exchangeMailFrmt

  def getMailsForMailbox(userId: Int, mailboxId: Int) = CRMActionAsync{rq =>
    // if(rq.header.belongsToCompany(companyId)){
    ExchangeODSMailDBRepository.getMailsByMailboxId(mailboxId).map(mails => Json.toJson(mails))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  def getMail(userId: Int, mailboxId: Int, mailId: Int)  = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    ExchangeODSMailDBRepository.getMailById(mailId).map(mail => Json.toJson(mail))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
}
