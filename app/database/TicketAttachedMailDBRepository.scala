package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, TicketActionAttachedMail , PagedResult}
import play.api.Logger
import utils.converters.TicketActionConverter._
import database.tables.TicketActionAttachedMailEntity


object TicketAttachedMailDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAttachedMailAction(action: TicketActionAttachedMail): Future[TicketActionAttachedMail] = {
    insertAttachedMailEnitity(action.asAttachedActionMailEntity)
          .map(inserted => inserted.asAttachedMailAction)
  }

  def deleteAttachedMailAction(actionId: Int, mailId: Int): Future[TicketActionAttachedMail] = {
    deleteAttachedMailEntity(TicketActionAttachedMailEntity(actionId,mailId)).map(deleted => deleted.asAttachedMailAction)
  }
}
