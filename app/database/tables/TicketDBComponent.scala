package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class TicketEntity(
  id: Option[Int] = None,
  companyId: Int,
  createdByUserId: Int,
  requestedByUserId: Int,
  assignedToUserId: Int,
  assignedToTeamId: Option[Int] = None,
  commentId: Option[Int] = None,
  status: Int,
  subject: String,
  description: Option[String] = None,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait TicketDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent
    with UserDBComponent
    with TeamDBComponent
    with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val tickets = TableQuery[TicketTable]
  
  class TicketTable(tag: Tag) extends Table[TicketEntity](tag, "tbl_ticket") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def createdByUserId = column[Int]("created_by_user_id")
    def requestedByUserId = column[Int]("requested_by_user_id")
    def assignedToUserId = column[Int]("assigned_to_user_id")
    def assignedToTeamId = column[Int]("assigned_to_team_id", Nullable)
    def commentId = column[Int]("comment_id", Nullable)
    def status = column[Int]("status", O.SqlType("VARCHAR(255)"))
    def subject = column[String]("subject", O.SqlType("VARCHAR(255)"))
    def description = column[String]("description", Nullable)
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))


    def fkCompanyId = foreignKey("fk_ticket_company", companyId, companies)(_.id)
    def fkCreatedByUserId = foreignKey("fk_ticket_created_by_user_id", createdByUserId, users)(_.id)
    def fkRequestedByUserId = foreignKey("fk_ticket_requested_by_user_id", requestedByUserId, users)(_.id)
    def fkAssignedToUserId = foreignKey("fk_ticket_assigned_to_user_id", assignedToUserId, users)(_.id)
    def fkAssignedToTeamId = foreignKey("fk_ticket_assigned_to_team_id", assignedToTeamId, teams)(_.id)
    def fkCommentId = foreignKey("fk_ticket_comment_id", commentId, actions)(_.id)


    def * = (id.?, companyId, createdByUserId, requestedByUserId, assignedToUserId, assignedToTeamId.?, commentId.?, status, subject,
             description.?, recordStatus, createdAt, updatedAt)<>(TicketEntity.tupled, TicketEntity.unapply)

  }

  def ticketQry(companyId: Int) = {

    tickets.filter(t =>(t.companyId === companyId && 

                        t.recordStatus === RowStatus.ACTIVE))
  }


  //CRUD TicketEntity
  def insertTicket(ticket: TicketEntity): Future[TicketEntity] = {
      db.run((tickets returning tickets.map(_.id) into ((ticket,id) => ticket.copy(id=Some(id)))) += ticket)
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

  //FILTERS
  def getTicketEntitiesByCompanyId(companyId: Int): Future[List[TicketEntity]] = {
    db.run(tickets.filter(t => (t.companyId === companyId && 
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
}

