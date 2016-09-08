package database


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Mailbox, PagedResult}
import play.api.Logger
import utils.converters.MailboxConverter._


object MailboxDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._
  
//  def saveMailbox(mailBox: Mailbox): Future[Mailbox] = {
  def saveMailbox(mailBox: Mailbox, userId: Int): Future[Int] = {
//    insertMailboxEnitity(mailBox.asMailboxEntity).map(res => res.asMailbox)
    insertMailboxEnitity(mailBox.asMailboxEntity, userId)
  }

  def getMailboxById(id: Int): Future[Mailbox] = {
    getMailboxEntityById(id).map(res => res.asMailbox)
  }

  def getAllMailboxesByUserId(userId: Int): Future[List[Mailbox]] = {
    getMailboxEntitiesByUserId(userId).map(list => list.map(_.asMailbox))
  }

  def getUserIdByMailboxId(mailboxId: Int): Future[Int] = {
    getMailboxEntityById(mailboxId).map(res => res.userId)
  }

  def updateMailbox(mailBox: Mailbox): Future[Mailbox] = {
    updateMailboxEntity(mailBox.asMailboxEntity).map(res => res.asMailbox)
  }

  def softDeleteMailboxById(id: Int): Future[Mailbox] = {
    softDeleteMailboxEntityById(id).map(res => res.asMailbox)
  }

  def searchMailboxByName(userId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Mailbox]] = {
    searchMailboxEntitiesByName(userId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Mailbox](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asMailbox))}
  }

}
