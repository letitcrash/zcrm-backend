package database.tables

import java.sql.Timestamp
import models.{RowStatus,TicketStatus, TicketPriority}
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

case class TicketEntity(
                         id: Option[Int] = None,
                         companyId: Int,
                         projectId: Option[Int] = None,
                         createdByUserId: Int,
                         requestedByUserId: Option[Int] = None,
                         //requestedByEmail: Option[String] = None,
                         assignedToUserId: Option[Int] = None,
                         assignedToTeamId: Option[Int] = None,
                         status: Int,
                         priority: Int,
                         subject: String,
                         description: Option[String] = None,
                         recordStatus: Int = RowStatus.ACTIVE,
                         deadline: Option[Timestamp] = None,
                         createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
                         updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait TicketDBComponent extends DBComponent {
    this: DBComponent 
    with ProjectDBComponent
    with CompanyDBComponent
    with UserDBComponent
    with TeamDBComponent
    with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val tickets = TableQuery[TicketTable]
  
  class TicketTable(tag: Tag) extends Table[TicketEntity](tag, "tbl_ticket") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id", Nullable)
    def projectId = column[Int]("project_id", Nullable)
    def createdByUserId = column[Int]("created_by_user_id")
    def requestedByUserId = column[Int]("requested_by_user_id", Nullable)
    def assignedToUserId = column[Int]("assigned_to_user_id", Nullable)
    def assignedToTeamId = column[Int]("assigned_to_team_id", Nullable)
    def status = column[Int]("status", O.Default(TicketStatus.NEW))
    def priority = column[Int]("priority", O.Default(TicketPriority.MID))
    def subject = column[String]("subject", O.SqlType("VARCHAR(255)"))
    def description = column[String]("description", Nullable)
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def deadline = column[Timestamp]("deadline", Nullable)
    def createdAt = column[Timestamp]("created_at", Nullable)
    def updatedAt = column[Timestamp]("updated_at", Nullable)


    def fkProjectId = foreignKey("fk_ticket_project", projectId, projects)(_.id)
    def fkCreatedByUserId = foreignKey("fk_ticket_created_by_user_id", createdByUserId, users)(_.id)
    def fkRequestedByUserId = foreignKey("fk_ticket_requested_by_user_id", requestedByUserId, users)(_.id)
    def fkAssignedToUserId = foreignKey("fk_ticket_assigned_to_user_id", assignedToUserId, users)(_.id)
    def fkAssignedToTeamId = foreignKey("fk_ticket_assigned_to_team_id", assignedToTeamId, teams)(_.id)


    def * = (id.?, companyId, projectId.?, createdByUserId, requestedByUserId.?, assignedToUserId.?, assignedToTeamId.?, status, priority, subject,
             description.?, recordStatus, deadline.?, createdAt, updatedAt)<>(TicketEntity.tupled, TicketEntity.unapply)

  }

  //JOINS
  //(TicketEntity, (UserEntity, ContactProfileEntity))
  def aggregatedTickets = tickets join usersWithProfile on (_.createdByUserId === _._1.id)
                          
  def ticketQry(companyId: Int, 
                projectIds: List[Int], 
                createdByUserIds: List[Int],
                requestedByUserIds: List[Int], 
                assignedToUserIds: List[Int],
                assignedToTeamIds: List[Int]) = {
    tickets.filter(t =>(t.companyId === companyId && t.recordStatus === RowStatus.ACTIVE))
      .filteredBy( projectIds match { case List() => None; case list => Some(list) } )( _.projectId inSet _)
      .filteredBy( createdByUserIds match { case List() => None; case list => Some(list) } )( _.createdByUserId inSet _)
      .filteredBy( requestedByUserIds match { case List() => None; case list => Some(list) } )( _.requestedByUserId inSet _)
      .filteredBy( assignedToUserIds match { case List() => None; case list => Some(list) } )( _.assignedToUserId inSet _)
      .filteredBy( assignedToTeamIds match { case List() => None; case list => Some(list) } )( _.assignedToTeamId inSet _)
  }

  //CRUD TicketEntity
  def insertTicket(ticket: TicketEntity): Future[TicketEntity] = {
      db.run((tickets returning tickets.map(_.id) into ((ticket,id) => ticket.copy(id=Some(id)))) += ticket)
  }

  def getAggTicketEntityById(id: Int): Future[(TicketEntity, (UserEntity, ContactProfileEntity))] = {
    db.run(aggregatedTickets.filter(t =>(t._1.id === id &&
                               t._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getTicketEntityById(id: Int): Future[TicketEntity] = {
    db.run(tickets.filter(t =>(t.id === id &&
                               t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateTicketEntity(ticket: TicketEntity): Future[TicketEntity] = {
      db.run(tickets.filter(t =>(t.id === ticket.id &&
                                t.recordStatus === RowStatus.ACTIVE)).update(ticket)
                  .map{num => if(num != 0) ticket
                              else throw new Exception("Can't update ticket, is it deleted?")})
  }

  def softDeleteTicketById(id: Int): Future[TicketEntity] = {
    getTicketEntityById(id).flatMap(res =>
        updateTicketEntity(res.copy(recordStatus = RowStatus.DELETED,
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  }

  def deleteTicketsByProjectId(projectId: Int): Future[List[TicketEntity]] = {
    getTicketEntitiesByProjectId(projectId).flatMap(tickets =>
      Future.sequence(tickets.map(ticket => updateTicketEntity(ticket.copy(recordStatus = RowStatus.DELETED,
                                                               updatedAt = new Timestamp(System.currentTimeMillis()))))))
  }

  //FILTERS
  def getTicketEntitiesByCompanyId(companyId: Int): Future[List[TicketEntity]] = {
    db.run(tickets.filter(t => (t.companyId === companyId &&
                                t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getTicketEntitiesByCreatedByUserId(userId: Int): Future[List[TicketEntity]] = {
    db.run(tickets.filter(t => (t.createdByUserId === userId &&
                                t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getTicketEntitiesByProjectId(projectId: Int): Future[List[TicketEntity]] = {
    db.run(tickets.filter(t => (t.projectId === projectId &&
                                t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }


  def searchTicketEntitiesByName(companyId: Int, 
                                 projectIds: List[Int], 
                                 createdByUserIds: List[Int],
                                 requestedByUserIds: List[Int], 
                                 assignedToUserIds: List[Int],
                                 assignedToTeamIds: List[Int], 
                                 pageSize: Int, 
                                 pageNr: Int, 
                                 searchTerm: Option[String] = None): Future[PagedDBResult[TicketEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        ticketQry(companyId,
                  projectIds,
                  createdByUserIds,
                  requestedByUserIds,
                  assignedToUserIds,
                  assignedToTeamIds).filter{_.subject.like(s)}
      }.getOrElse(ticketQry(companyId,
                  projectIds,
                  createdByUserIds,
                  requestedByUserIds,
                  assignedToUserIds,
                  assignedToTeamIds))  

    val pageRes = baseQry
      .sortBy(_.subject.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( ticketList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = ticketList)
          )
        )
  }


  //Counts
  def getCountNewTicket(projectId: Int): Future[Int] = {
   db.run(tickets.filter(s => (s.projectId === projectId && 
                               s.status === TicketStatus.NEW)).length.result)
  }

  def getCountOpenTicket(projectId: Int): Future[Int] = {
   db.run(tickets.filter(s => (s.projectId === projectId && 
                               s.status === TicketStatus.OPEN)).length.result)
  }

  def getCountPostponedTicket(projectId: Int): Future[Int] = {
   db.run(tickets.filter(s => (s.projectId === projectId && 
                               s.status === TicketStatus.POSTPONED)).length.result)
  }
  
  def getCountResolvedTicket(projectId: Int): Future[Int] = {
   db.run(tickets.filter(s => (s.projectId === projectId && 
                               s.status === TicketStatus.RESOLVED)).length.result)
  }

  def getTicketsCountByCompanyId(companyId: Int): Future[Int] = {
      db.run(tickets.filter(_.companyId === companyId).length.result)
  }

}

