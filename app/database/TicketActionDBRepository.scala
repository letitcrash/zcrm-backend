package database

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{TicketAction, PagedResult}
import play.api.Logger
import utils.converters.TicketActionConverter._


object TicketActionDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createAction(action: TicketAction, companyId: Int): Future[TicketAction] = {
    insertAction(action.asActionEntity)
          .flatMap(inserted => 
                   getActionEntityWithProfileById(inserted.id.get).map(_.asAction))
  }

  def updateAction(action: TicketAction): Future[TicketAction] = {
    updateActionEntity(action.asActionEntity)
          .flatMap(updated => 
                   getActionEntityWithProfileById(updated.id.get).map(_.asAction))
  }

  def deleteAction(actionId: Int): Future[TicketAction] = {
    softDeleteActionById(actionId)
          .flatMap(deleted => 
                   getActionEntityWithProfileById(deleted.id.get).map(_.asAction))
  }

  def getActionById(id: Int): Future[TicketAction] = {
    getActionEntityWithProfileById(id).map(action => action.asAction)
  }

  def getActionsByTicketId(ticketId: Int, actionTypes: List[Int]): Future[List[TicketAction]] = {
    getActionEntitiesByTicketId(ticketId, actionTypes).map(list => list.map(_.asAction))
  }

  def getActionsByUserId(userId: Int): Future[List[TicketAction]] = {
    getActionEntitiesByUserId(userId).map(list => list.map(_.asAction))
  }

  def getActionWithPagination(ticketId: Int, actionTypes: List[Int], pageSize: Int, pageNr: Int): Future[PagedResult[TicketAction]] = {
    getActionEntitiesWithPagination(ticketId, actionTypes, pageSize, pageNr).map{dbPage =>
        PagedResult[TicketAction](pageSize = dbPage.pageSize,
                                  pageNr = dbPage.pageNr,
                                  totalCount = dbPage.totalCount,
                                  data = dbPage.data.map(_.asAction))}
  }

}
