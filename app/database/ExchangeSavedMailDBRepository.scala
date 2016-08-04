package database

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.converters.MailConverter._

import scala.concurrent.Future


object ExchangeSavedMailDBRepository {
  import database.gen.current.dao._

    def insertSavedMail(mail: ExchangeMail): Future[ExchangeMail] = {
      insertSavedMailEntity(mail.asSavedEntity).map(inserted => inserted.asMailFromSaved)
    }

    def getSavedMailById(id: Int): Future[ExchangeMail] = {
      getSavedMailEntityById(id).map(entity => entity.asMailFromSaved)
    }

    def getSavedMailByExtId(extId: String): Future[ExchangeMail] = {
      getSavedMailEntityByExtId(extId).map(entity => entity.asMailFromSaved)
    }

    def  getSavedMailsByConversationId(conversationId: String): Future[List[ExchangeMail]] = {
      getSavedMailEntitiesByConversationId(conversationId).map(list => list.map(_.asMailFromSaved))
    }

    def updateSavedMail(mail: ExchangeMail): Future[ExchangeMail] = {
        updateSavedMailEntity(mail.asSavedEntity).map(updated => updated.asMailFromSaved)
    }

    def deleteSavedMailById(id: Int): Future[ExchangeMail] = {
        deleteSavedMailEntityById(id).map(deleted => deleted.asMailFromSaved)
    }

    def deleteSavedMailByExtId(extId: String): Future[ExchangeMail] = {
        deleteSavedMailEntityByExtId(extId).map(deleted => deleted.asMailFromSaved)
    }
}
