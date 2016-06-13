package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Ticket, AggregatedTicket, PagedResult, User}
import play.api.Logger
import utils.converters.TicketConverter._


object TicketDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createTicket(ticket: Ticket, companyId: Int): Future[Ticket] = {
    for {
      ticketEntt <- insertTicket(ticket.asTicketEntity(companyId))
      members <- ticket.members.map( users => addMembers(ticketEntt.id.get, users)).getOrElse(Future())
    } yield ticketEntt.asTicket

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

  def searchTicketByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Ticket]] = {
    searchTicketEntitiesByName(companyId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Ticket](pageSize = dbPage.pageSize,
                            pageNr = dbPage.pageNr,
                            totalCount = dbPage.totalCount,
                            data = dbPage.data.map(_.asTicket))}
  }

//  def getAggregatedTicketById(ticketId: Int): Future[AggregatedTicket] = {
//    getAggregatedTicketEntityById(ticketId).map(aggTicket => aggTicket.asTicket)
//  }
//
//  def getAllAggregatedTickets(companyId: Int,
//                              createdByUserIds: List[Int],
//                              //requestedByUserIds: List[Int],
//                              assignedToUserIds: List[Int],
//                              assignedToTeamIds: List[Int],
//                              pageSize: Option[Int],
//                              pageNr: Option[Int]): Future[PagedResult[AggregatedTicket]] = {
//    getAllAggregatedTicketsByCompanyId(companyId, createdByUserIds, assignedToUserIds, assignedToTeamIds, pageSize,pageNr)
//          .map(dbPage => PagedResult[AggregatedTicket](pageSize = dbPage.pageSize,
//                                                       pageNr = dbPage.pageNr,
//                                                       totalCount = dbPage.totalCount,
//                                                       data = dbPage.data.map(_.asTicket)))
//  }
//
//  def updateAggregatedTicket(aggTicket: AggregatedTicket): Future[AggregatedTicket] = {
//    updateTicketEntity(aggTicket.asTicketEntity)
//          .flatMap(updated => getAggregatedTicketById(updated.id.get))
//
//  }
  

  def addMembers(ticketId: Int, users: List[User]): Future[List[User]] = {
    deleteAllMembersByTicketId(ticketId).flatMap(count =>
      insertTicketMembers(users.map( u => (ticketId, u.id.get).asTicketMemberEntt))
        .map(pair => users))
  }

}
