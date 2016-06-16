package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Ticket, Team, PagedResult, User, Client}
import play.api.Logger
import utils.converters.TicketConverter._


object TicketDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createTicket(ticket: Ticket, companyId: Int): Future[Ticket] = {
    for {
      ticketEntt <- insertTicket(ticket.asTicketEntity(companyId))
      members <- ticket.members.map( users => addMembers(ticketEntt.id.get, users)).getOrElse(Future())
      teams <- ticket.teams.map( teams => addTeams(ticketEntt.id.get, teams)).getOrElse(Future())
      clients <- ticket.clients.map( clients => addClients(ticketEntt.id.get, clients)).getOrElse(Future())
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
    for {
    ticket <- getAggTicketEntityById(id)
    userEntts <- getUsersByTicketId(ticket._1.id.get)
    teamsEntts <- getTeamsByTicketId(ticket._1.id.get)
    clientEntts <- getClientsByTicketId(ticket._1.id.get)
    } yield ticket.asTicket(userEntts, teamsEntts, clientEntts)
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

  def addMembers(ticketId: Int, users: List[User]): Future[List[User]] = {
    deleteAllMembersByTicketId(ticketId).flatMap(count =>
      insertTicketMembers(users.map( u => (ticketId, u.id.get).asTicketMemberEntt))
        .map(pair => users))
  }

  def addTeams(ticketId: Int, teams: List[Team]): Future[List[Team]] = {
    deleteAllTeamsByTicketId(ticketId).flatMap(count => 
      insertTicketTeamMembers(teams.map( t => (ticketId, t.id.get).asTicketTeamMemberEntt))
        .map( pair => teams))
  }

  def addClients(ticketId: Int, clients: List[Client]): Future[List[Client]] = {
    deleteAllClientsByTicketId(ticketId).flatMap(count => 
      insertTicketClients(clients.map( c => (ticketId, c.id.get).asTicketClientEntt))
        .map( pair => clients))
  }
}
