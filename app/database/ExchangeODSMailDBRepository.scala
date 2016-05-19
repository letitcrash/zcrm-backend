package database

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.converters.MailConverter._

import scala.concurrent.Future


object ExchangeODSMailDBRepository {
  import database.gen.current.dao._

    def insertMail(mail: ExchangeMail): Future[ExchangeMail] = {
        insertODSMailEntity(mail.asEntity).map(inserted => inserted.asMail)
    }

    def getMailById(id: Int): Future[ExchangeMail] = {
      getODSMailEntityById(id).map(entity => entity.asMail)
    }

    def getMailByExtId(extId: String): Future[ExchangeMail] = {
      getODSMailEntityByExtId(extId).map(entity => entity.asMail)
    }

    def  getMailsByConversationId(conversationId: String): Future[List[ExchangeMail]] = {
      getODSMailEntitiesByConversationId(conversationId).map(list => list.map(_.asMail))
    }

    def getMailsByMailboxId(mailboxId: Int): Future[List[ExchangeMail]] = {
        getODSMailEntitiesByMailboxId(mailboxId).map(list => list.map(_.asMail))
    }

    def updateMail(mail: ExchangeMail): Future[ExchangeMail] = {
        updateODSMailEntity(mail.asEntity).map(updated => updated.asMail)
    }

    def deleteMailById(id: Int): Future[ExchangeMail] = {
        deleteODSMailEntityById(id).map(deleted => deleted.asMail)
    }

    def deleteMailByExtId(extId: String): Future[ExchangeMail] = {
        deleteODSMailEntityByExtId(extId).map(deleted => deleted.asMail)
    }
}
