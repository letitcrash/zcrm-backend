package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.Logger

case class TicketClientEntity(
  ticketId: Int,
  clientId: Int)

trait TicketClientDBComponent extends DBComponent { 
  this: DBComponent 
  with TicketDBComponent
  with ClientDBComponent => 

  import dbConfig.driver.api._

  val ticketClients = TableQuery[TicketClientTable]

  class TicketClientTable(tag: Tag) extends Table[TicketClientEntity](tag, "tbl_ticket_client") {
    def ticketId = column[Int]("ticket_id")
    def clientId = column[Int]("client_id")
    def fkTicket = foreignKey("fk_ticket_member_ticket", ticketId, tickets)(_.id)
    def fkUser = foreignKey("fk_ticket_client_client", clientId, clients)(_.id)
    override def * = (ticketId, clientId) <> (TicketClientEntity.tupled, TicketClientEntity.unapply)
  }

  //JOINS 
  //(TicketClientEntity, (ClientEntity, ContactProfileEntity))
  def ticketClientWithProfile = ticketClients join clientWithProfile on (_.clientId === _._1.id)

  //CRUD
  def insertTicketClient(entity: TicketClientEntity): Future[TicketClientEntity] = {
    db.run(ticketClients += entity).map( res => entity)
  }

  def deleteTicketClient(entity: TicketClientEntity): Future[TicketClientEntity] = {
    db.run(ticketClients.filter( t => ( t.ticketId === entity.ticketId &&
                                  t.clientId === entity.clientId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertTicketClients(ticketClients: List[TicketClientEntity]): Future[List[TicketClientEntity]] = {
    Future.sequence(ticketClients.map( c =>  insertTicketClient(c)))
  }

  def deleteTicketClients(ticketClients: List[TicketClientEntity]): Future[List[TicketClientEntity]] = {
    Future.sequence(ticketClients.map( c =>  deleteTicketClient(c)))
    Future(ticketClients)
  }

  def deleteAllClientsByTicketId(ticketId: Int): Future[Int] = {
    db.run(ticketClients.filter(t => ( t.ticketId === ticketId)).delete)
  }

  def getClientsByTicketId(ticketId: Int): Future[List[(TicketClientEntity, (ClientEntity, ContactProfileEntity))]] = {
    db.run(ticketClientWithProfile.filter( _._1.ticketId === ticketId).result).map(_.toList)
  }


}
