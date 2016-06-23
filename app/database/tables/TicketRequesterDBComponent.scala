package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class TicketRequesterEntity(
  ticketId: Int,
  userId: Int)

trait TicketRequesterDBComponent extends DBComponent { 
  this: DBComponent with ContactProfileDBComponent
  with TicketDBComponent
  with UserDBComponent => 

  import dbConfig.driver.api._

  val ticketRequesters = TableQuery[TicketRequesterTable]

  class TicketRequesterTable(tag: Tag) extends Table[TicketRequesterEntity](tag, "tbl_ticket_requester") {
    def ticketId = column[Int]("ticket_id")
    def userId = column[Int]("user_id")
    def fkTicket = foreignKey("fk_ticket_requester_ticket", ticketId, tickets)(_.id)
    def fkUser = foreignKey("fk_ticket_requtester_user", userId, users)(_.id)
    override def * = (ticketId, userId) <> (TicketRequesterEntity.tupled, TicketRequesterEntity.unapply)
  }
  //JOINS 

  //(TicketRequesterEntity, (UserEntity, ContactProfileEntity))
  def ticketRequestersWithUser = ticketRequesters join usersWithProfile on (_.userId === _._1.id)

  //CRUD
  def insertTicketRequester(entity: TicketRequesterEntity): Future[TicketRequesterEntity] = {
    db.run(ticketRequesters += entity).map( res => entity)
  }

  def deleteTicketRequester(entity: TicketRequesterEntity): Future[TicketRequesterEntity] = {
    db.run(ticketRequesters.filter( t => ( t.ticketId === entity.ticketId &&
                                  t.userId === entity.userId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertTicketRequesters(ticketRequesters: List[TicketRequesterEntity]): Future[List[TicketRequesterEntity]] = {
    Future.sequence(ticketRequesters.map( d =>  insertTicketRequester(d)))
  }

  def deleteTicketRequesters(ticketRequesters : List[TicketRequesterEntity]): Future[List[TicketRequesterEntity]] = {
    Future.sequence(ticketRequesters.map( t =>  deleteTicketRequester(t)))
    Future(ticketRequesters)
  }

  def deleteAllRequestersByTicketId(ticketId: Int): Future[Int] = {
    db.run(ticketRequesters.filter(t => ( t.ticketId === ticketId)).delete)
  }

  def getRequestersByTicketId(ticketId: Int): Future[List[(TicketRequesterEntity, (UserEntity, ContactProfileEntity))]] = {
    db.run(ticketRequestersWithUser.filter( _._1.ticketId === ticketId).result).map(_.toList)
  }

  

 
}
