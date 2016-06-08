package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, TicketActionAttachedMail , PagedResult}
import play.api.Logger
import utils.converters.TicketActionConverter._


object TicketAttachedMailDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAttachedMailAction(action: TicketActionAttachedMail): Future[TicketActionAttachedMail] = {
    insertAttachedMailEnitity(action.asAttachedActionEntity)
          .map(inserted => inserted.asAttachedMailAction)
  }

  def getAttachedMailAction(actionId: Int): Future[TicketActionAttachedMail] = {
    getAttachedMailEntityById(actionId).map(action => action.asAttachedMailAction)
  }

  def deleteAttachedMailAction(actionId: Int): Future[TicketActionAttachedMail] = {
    deleteAttachedMailEntityById(actionId).map(deleted => deleted.asAttachedMailAction)
  }
}
