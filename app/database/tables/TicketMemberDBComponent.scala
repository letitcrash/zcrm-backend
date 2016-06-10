package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class TicketMemberEntity(
  ticketId: Int,
  userId: Int)

trait TicketMemberDBComponent extends DBComponent { 
  this: DBComponent with ContactProfileDBComponent
  with TicketDBComponent
  with UserDBComponent => 

  import dbConfig.driver.api._

  val ticketMembers = TableQuery[TicketMemberTable]

  class TicketMemberTable(tag: Tag) extends Table[TicketMemberEntity](tag, "tbl_ticket_member") {
    def ticketId = column[Int]("ticket_id")
    def userId = column[Int]("user_id")
    def fkTicket = foreignKey("fk_ticket_member_ticket", ticketId, tickets)(_.id)
    def fkUser = foreignKey("fk_ticket_member_user", userId, users)(_.id)
    override def * = (ticketId, userId) <> (TicketMemberEntity.tupled, TicketMemberEntity.unapply)
  }
  
  //CRUD
  def insertTicketMember(entity: TicketMemberEntity): Future[TicketMemberEntity] = {
    db.run(ticketMembers += entity).map( res => entity)
  }

  def deleteTicketMember(entity: TicketMemberEntity): Future[TicketMemberEntity] = {
    db.run(ticketMembers.filter( t => ( t.ticketId === entity.ticketId &&
                                  t.userId === entity.userId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertTicketMembers(ticketMembers: List[TicketMemberEntity]): Future[List[TicketMemberEntity]] = {
    Future.sequence(ticketMembers.map( d =>  insertTicketMember(d)))
  }

  def deleteTicketMembers(ticketMembers : List[TicketMemberEntity]): Future[List[TicketMemberEntity]] = {
    Future.sequence(ticketMembers.map( t =>  deleteTicketMember(t)))
    Future(ticketMembers)
  }

}
