package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Ticket
import play.api.Logger
import utils.converters.TicketConverter._


object TicketDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createTicket(ticket: Ticket, companyId: Int): Future[Ticket] = {
    insertTicket(ticket.asTicketEntity(companyId))
          .map(inserted => inserted.asTicket)
  }

  def updateTicket(ticket: Ticket, companyId: Int): Future[Ticket] = {
    updateTicketEntity(ticket.asTicketEntity(companyId))
          .map(updated => updated.asTicket)
  }

  def deleteTicket(ticketId: Int): Future[Ticket] = {
    softDeleteTicketById(ticketId)
          .map(deleted => deleted.asTicket)
  }

  def getTicketById(id: Int): Future[Ticket] = {
    getTicketEntityById(id).map(ticket => ticket.asTicket)
  }

  def getTicketsByCompanyId(companyId: Int): Future[List[Ticket]] = {
    getTicketEntitiesByCompanyId(companyId).map(list => list.map(_.asTicket))
  } 
  

}
