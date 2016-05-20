package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.TicketAction
import play.api.Logger
import utils.converters.ActionConverter._


object TicketActionDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAction(action: TicketAction, companyId: Int): Future[TicketAction] = {
    insertAction(action.asActionEntity(companyId))
          .map(inserted => inserted.asAction)
  }

  def updateAction(action: TicketAction, companyId: Int): Future[TicketAction] = {
    updateActionEntity(action.asActionEntity(companyId))
          .map(updated => updated.asAction)
  }

  def deleteAction(actionId: Int): Future[TicketAction] = {
    softDeleteActionById(actionId)
          .map(deleted => deleted.asAction)
  }

  def getActionById(id: Int): Future[TicketAction] = {
    getActionEntityById(id).map(action => action.asAction)
  }

  def getActionsByTicketId(ticketId: Int): Future[List[TicketAction]] = {
    getActionEntitiesByTicketId(ticketId).map(list => list.map(_.asAction))
  }

  def getActionsByUserId(userId: Int): Future[List[TicketAction]] = {
    getActionEntitiesByUserId(userId).map(list => list.map(_.asAction))
  }


}
