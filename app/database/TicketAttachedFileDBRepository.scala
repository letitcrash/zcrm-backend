package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, TicketActionAttachedFile , PagedResult}
import play.api.Logger
import utils.converters.TicketActionConverter._
import database.tables.TicketActionAttachedFileEntity


object TicketAttachedFileDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAttachedFileAction(action: TicketActionAttachedFile): Future[TicketActionAttachedFile] = {
    insertAttachedFileEnitity(action.asAttachedActionFileEntity)
          .map(inserted => inserted.asAttachedFileAction)
  }

  def deleteAttachedFileAction(actionId: Int, fileId: Int): Future[TicketActionAttachedFile] = {
    deleteAttachedFileEntity(TicketActionAttachedFileEntity(actionId,fileId)).map(deleted => deleted.asAttachedFileAction)
  }
}
