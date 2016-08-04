package database

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.converters.MailConverter._

import scala.concurrent.Future


object ExchangeODSMailDBRepository {
  import database.gen.current.dao._

    def insertODSMail(mail: ExchangeMail): Future[ExchangeMail] = {
        insertODSMailEntity(mail.asODSEntity).map(inserted => inserted.asMailFromODS)
    }

    def getODSMailById(id: Int): Future[ExchangeMail] = {
      getODSMailEntityById(id).map(entity => entity.asMailFromODS)
    }

    def getODSMailByExtId(extId: String): Future[ExchangeMail] = {
      getODSMailEntityByExtId(extId).map(entity => entity.asMailFromODS)
    }

    def getUserIdByODSMailId(mailId: Int): Future[Int] = {
      getODSMailEntityById(mailId).flatMap(entity => 
                                       getMailboxEntityById(entity.id.get).map(res => res.userId))
    }

    def  getODSMailsByConversationId(conversationId: String): Future[List[ExchangeMail]] = {
      getODSMailEntitiesByConversationId(conversationId).map(list => list.map(_.asMailFromODS))
    }

    def getODSMailsByMailboxId(mailboxId: Int): Future[List[ExchangeMail]] = {
        getODSMailEntitiesByMailboxId(mailboxId).map(list => list.map(_.asMailFromODS))
    }

    def updateODSMail(mail: ExchangeMail): Future[ExchangeMail] = {
        updateODSMailEntity(mail.asODSEntity).map(updated => updated.asMailFromODS)
    }

    def deleteODSMailById(id: Int): Future[ExchangeMail] = {
        deleteODSMailEntityById(id).map(deleted => deleted.asMailFromODS)
    }

    def deleteODSMailByExtId(extId: String): Future[ExchangeMail] = {
        deleteODSMailEntityByExtId(extId).map(deleted => deleted.asMailFromODS)
    }

  def searchODSMailsByMailboxId(mailboxId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[GroupedMail]] = {
    searchODSMailEntitiesByMailboxId(mailboxId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[GroupedMail](pageSize = dbPage.pageSize,
                                  pageNr = dbPage.pageNr,
                                  totalCount = dbPage.totalCount,
                                  data = dbPage.data.groupBy(_.conversationExtId).map{ case (k,v) => GroupedMail(conversationId = k, mails = v.map(_.asMailFromODS).toList)}.toList)}
  }
}
