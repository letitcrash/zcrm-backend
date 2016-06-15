package database.tables

import java.sql.Timestamp
import models.{RowStatus,TicketStatus, TicketPriority}
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class TicketEntity(
                         id: Option[Int] = None,
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
                         createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
                         updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait TicketDBComponent extends DBComponent {
    this: DBComponent 
    with ProjectDBComponent
    with UserDBComponent
    with TeamDBComponent
    with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val tickets = TableQuery[TicketTable]
  
  class TicketTable(tag: Tag) extends Table[TicketEntity](tag, "tbl_ticket") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
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
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at")


    def fkProjectId = foreignKey("fk_ticket_project", projectId, projects)(_.id)
    def fkCreatedByUserId = foreignKey("fk_ticket_created_by_user_id", createdByUserId, users)(_.id)
    def fkRequestedByUserId = foreignKey("fk_ticket_requested_by_user_id", requestedByUserId, users)(_.id)
    def fkAssignedToUserId = foreignKey("fk_ticket_assigned_to_user_id", assignedToUserId, users)(_.id)
    def fkAssignedToTeamId = foreignKey("fk_ticket_assigned_to_team_id", assignedToTeamId, teams)(_.id)


    def * = (id.?, projectId.?, createdByUserId, requestedByUserId.?, assignedToUserId.?, assignedToTeamId.?, status, priority, subject,
             description.?, recordStatus, createdAt, updatedAt)<>(TicketEntity.tupled, TicketEntity.unapply)

  }

  //JOINS
  //(TicketEntity, (UserEntity, ContactProfileEntity))
  def aggregatedTickets = tickets join usersWithProfile on (_.createdByUserId === _._1.id)
                          

/*
//(((((TicketEntity, (CompanyEntity, ContactProfileEntity)), Option[(UserEntity, ContactProfileEntity)]), Option[(UserEntity, ContactProfileEntity)]), Option[(UserEntity, ContactProfileEntity)]), Option[TeamEntity])
  def aggregatedTickets = tickets join
                         companyWithProfile on (_.projectId === _._1.id ) joinLeft
                         usersWithProfile on (_._1.createdByUserId === _._1.id) joinLeft
                         usersWithProfile on (_._1._1.requestedByUserId === _._1.id) joinLeft
                         usersWithProfile on (_._1._1._1.assignedToUserId === _._1.id) joinLeft
                         teams on (_._1._1._1._1.assignedToTeamId === _.id)

  def aggregatedTicketQry(companyId: Int,
                          createdByUserIds: List[Int],
                          //requestedByUserIds: List[Int],
                          assignedToUserIds: List[Int],
                          assignedToTeamIds: List[Int]) = {
    aggregatedTickets.filter(t =>(t._1._1._1._1._1.projectId === companyId && t._1._1._1._1._1.recordStatus === RowStatus.ACTIVE))
      .filteredBy( createdByUserIds match { case List() => None; case list => Some(list) } )( _._1._1._1._1._1.createdByUserId inSet _)
     // .filteredBy( requestedByUserIds match { case List() => None; case list => Some(list) } )( _._1._1._1._1._1.requestedByUserId inSet _)
      .filteredBy( assignedToUserIds match { case List() => None; case list => Some(list) } )( _._1._1._1._1._1.assignedToUserId inSet _)
      .filteredBy( assignedToTeamIds match { case List() => None; case list => Some(list) } )( _._1._1._1._1._1.assignedToTeamId inSet _)
   }
*/

  def ticketQry(companyId: Int) = {
    tickets.filter(t =>(t.projectId === companyId &&
                        t.recordStatus === RowStatus.ACTIVE))
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

/*  def getAggregatedTicketEntityById(id: Int): Future[(((((TicketEntity, (CompanyEntity, ContactProfileEntity)), Option[(UserEntity, ContactProfileEntity)]),
                                                     Option[(UserEntity, ContactProfileEntity)]), Option[(UserEntity, ContactProfileEntity)]), Option[TeamEntity])] = {
    db.run(aggregatedTickets.filter(t => (t._1._1._1._1._1.id === id &&
                                          t._1._1._1._1._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }
*/

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

  //FILTERS
  def getTicketEntitiesByCompanyId(companyId: Int): Future[List[TicketEntity]] = {
    db.run(tickets.filter(t => (t.projectId === companyId &&
                                t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def searchTicketEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[TicketEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        ticketQry(companyId).filter{_.subject.like(s)}
      }.getOrElse(ticketQry(companyId))  

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
/*
  def getAllAggregatedTicketsByCompanyId(companyId: Int,
                                         createdByUserIds: List[Int],
                                         //requestedByUserIds: List[Int],
                                         assignedToUserIds: List[Int],
                                         assignedToTeamIds: List[Int],
                                         pageSize: Option[Int],
                                         pageNr: Option[Int])
   : Future[PagedDBResult[(((((TicketEntity, (CompanyEntity, ContactProfileEntity)), Option[(UserEntity, ContactProfileEntity)]),
                          Option[(UserEntity, ContactProfileEntity)]), Option[(UserEntity, ContactProfileEntity)]), Option[TeamEntity])]]= {
    val baseQry = aggregatedTicketQry(companyId,
                                      createdByUserIds,
                                      //requestedByUserIds,
                                      assignedToUserIds,
                                      assignedToTeamIds)
    var pageRes =
    if (pageNr.nonEmpty || pageSize.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)

      baseQry.sortBy(_._1._1._1._1._1.id.asc)
             .drop(psize * (pnr - 1))
             .take(psize)
    }else{ baseQry.sortBy(_._1._1._1._1._1.id.asc) }

    db.run(pageRes.result).flatMap( list =>
        db.run(baseQry.length.result).map( totalCount =>
         PagedDBResult(
            pageSize = pageSize.get,
            pageNr = pageNr.get,
            totalCount = totalCount,
            data = list)
          )
        )
  }
*/
}

