package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Action
import play.api.Logger
import utils.converters.ActionConverter._


object ActionDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAction(action: Action, companyId: Int): Future[Action] = {
    insertAction(action.asActionEntity(companyId))
          .map(inserted => inserted.asAction)
  }

  def updateAction(action: Action, companyId: Int): Future[Action] = {
    updateActionEntity(action.asActionEntity(companyId))
          .map(updated => updated.asAction)
  }

  def deleteAction(actionId: Int): Future[Action] = {
    softDeleteActionById(actionId)
          .map(deleted => deleted.asAction)
  }

  def getActionById(id: Int): Future[Action] = {
    getActionEntityById(id).map(action => action.asAction)
  }

  def getActionsByTicketId(ticketId: Int): Future[List[Action]] = {
    getActionEntitiesByTicketId(ticketId).map(list => list.map(_.asAction))
  }

  def getActionsByUserId(userId: Int): Future[List[Action]] = {
    getActionEntitiesByUserId(userId).map(list => list.map(_.asAction))
  }


}
