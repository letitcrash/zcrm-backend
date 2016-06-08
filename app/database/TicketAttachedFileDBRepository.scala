package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, TicketActionAttachedFile , PagedResult}
import play.api.Logger
import utils.converters.TicketActionConverter._


object TicketAttachedFileDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAttachedFileAction(action: TicketActionAttachedFile): Future[TicketActionAttachedFile] = {
    insertAttachedFileEnitity(action.asAttachedActionFileEntity)
          .map(inserted => inserted.asAttachedFileAction)
  }

  def getAttachedFileAction(actionId: Int): Future[TicketActionAttachedFile] = {
    getAttachedFileEntityById(actionId).map(action => action.asAttachedFileAction)
  }

  def deleteAttachedFileAction(actionId: Int): Future[TicketActionAttachedFile] = {
    deleteAttachedFileEntityById(actionId).map(deleted => deleted.asAttachedFileAction)
  }
}
