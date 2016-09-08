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

  def createTicket(ticket: Ticket, companyId: Int, projectId: Int): Future[Ticket] = {
    for {
      ticketEntt <- insertTicket(ticket.asTicketEntity(companyId, projectId))
      members <- ticket.members.map( users => addMembers(ticketEntt.id.get, users)).getOrElse(Future())
      teams <- ticket.teams.map( teams => addTeams(ticketEntt.id.get, teams)).getOrElse(Future())
      clients <- ticket.clients.map( clients => addClients(ticketEntt.id.get, clients)).getOrElse(Future())
    } yield ticketEntt.asTicket

  }

  def updateTicket(ticket: Ticket, companyId: Int): Future[Ticket] = {
    import utils.converters.ProjectConverter._
    for{
       updProject     <- updateProjectEntity(ticket.project.get.asProjectEntity)
       updTicket      <- updateTicketEntity(ticket.copy(project = Some(updProject.asProject())).asTicketEntity(companyId))
       aggTicket      <- getAggTicketEntityById(updTicket.id.get)
       userEntts      <- getUsersByTicketId(aggTicket._1.id.get)
       teamsEntts     <- getTeamsByTicketId(aggTicket._1.id.get)
       clientEntts    <- getClientsByTicketId(aggTicket._1.id.get)
       requesterEntts <- getRequestersByTicketId(aggTicket._1.id.get)
    }yield aggTicket.asTicket(userEntts, teamsEntts, clientEntts, requesterEntts, Some(updProject)) 
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
      requesterEntts <- getRequestersByTicketId(ticket._1.id.get)
      projectEntt <- ticket._1.projectId match {
        case Some(pId) => getProjectEntityById(pId).map(p => Some(p));
        case _ => Future(None)
      }
    } yield ticket.asTicket(userEntts, teamsEntts, clientEntts, requesterEntts, projectEntt)
  }



  def getTicketsByCompanyId(companyId: Int): Future[List[Ticket]] = {
    getTicketEntitiesByCompanyId(companyId).flatMap{list => 
      Future.sequence(list.map(t =>
        for {
          ticket <- getAggTicketEntityById(t.id.get)
          userEntts <- getUsersByTicketId(ticket._1.id.get)
          teamsEntts <- getTeamsByTicketId(ticket._1.id.get)
          clientEntts <- getClientsByTicketId(ticket._1.id.get)
          requesterEntts <- getRequestersByTicketId(ticket._1.id.get)
          projectEntt <- ticket._1.projectId match { case Some(pId) => getProjectEntityById(pId).map(p => Some(p)); case _ => Future(None) }
        } yield ticket.asTicket(userEntts, teamsEntts, clientEntts, requesterEntts, projectEntt)))}
  } 

  def getTicketsByCreatedByUserId(userId: Int): Future[List[Ticket]] = {
    getTicketEntitiesByCreatedByUserId(userId).flatMap{list => 
      Future.sequence(list.map(t =>
        for {
          ticket <- getAggTicketEntityById(t.id.get)
          userEntts <- getUsersByTicketId(ticket._1.id.get)
          teamsEntts <- getTeamsByTicketId(ticket._1.id.get)
          clientEntts <- getClientsByTicketId(ticket._1.id.get)
          requesterEntts <- getRequestersByTicketId(ticket._1.id.get)
          projectEntt <- ticket._1.projectId match { case Some(pId) => getProjectEntityById(pId).map(p => Some(p)); case _ => Future(None) }
        } yield ticket.asTicket(userEntts, teamsEntts, clientEntts, requesterEntts, projectEntt)))}
  } 

  def searchTicketByName(
      companyId: Int, 
      projectIds: List[Int], 
      createdByUserIds: List[Int],
      requestedByUserIds: List[Int], 
      assignedToUserIds: List[Int],
      assignedToTeamIds: List[Int], 
      pageSize: Int, 
      pageNr: Int, 
      searchTerm: Option[String],
      priority: Option[String],
      sort: String,
      order: String): Future[PagedResult[Ticket]] = {
    searchTicketEntitiesByName(
        companyId, 
        projectIds,
        createdByUserIds,
        requestedByUserIds,
        assignedToUserIds,
        assignedToTeamIds,
        pageSize, 
        pageNr, 
        searchTerm,
        priority,
        sort,
        order).flatMap { dbPage =>
      Future.sequence {
        dbPage.data.map { t =>
          for {
            ticket <- getAggTicketEntityById(t.id.get)
            userEntts <- getUsersByTicketId(ticket._1.id.get)
            teamsEntts <- getTeamsByTicketId(ticket._1.id.get)
            clientEntts <- getClientsByTicketId(ticket._1.id.get)
            requesterEntts <- getRequestersByTicketId(ticket._1.id.get)
            projectEntt <- ticket._1.projectId match {
              case Some(pId) => getProjectEntityById(pId).map(p => Some(p))
              case _ => Future(None)
            }
          } yield ticket.asTicket(userEntts, teamsEntts, clientEntts, requesterEntts, projectEntt)
        }
      } map(ticketList => PagedResult[Ticket](
          pageSize = dbPage.pageSize,
          pageNr = dbPage.pageNr,
          totalCount = dbPage.totalCount,
          data = ticketList))
    }
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

  def addRequesters(ticketId: Int, users: List[User]): Future[List[User]] = {
    deleteAllRequestersByTicketId(ticketId).flatMap(count =>
      insertTicketRequesters(users.map( u => (ticketId, u.id.get).asTicketRequesterEntt))
        .map(pair => users))
  }

}
