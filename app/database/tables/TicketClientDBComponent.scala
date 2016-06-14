package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class TicketClientrEntity(
  ticketId: Int,
  clientId: Int)

trait TicketClientDBComponent extends DBComponent { 
  this: DBComponent 
  with TicketDBComponent
  with ClientDBComponent => 

  import dbConfig.driver.api._

  val ticketClients = TableQuery[TicketClientTable]

  class TicketClientTable(tag: Tag) extends Table[TicketClientrEntity](tag, "tbl_ticket_client") {
    def ticketId = column[Int]("ticket_id")
    def clientId = column[Int]("client_id")
    def fkTicket = foreignKey("fk_ticket_member_ticket", ticketId, tickets)(_.id)
    def fkUser = foreignKey("fk_ticket_client_client", clientId, clients)(_.id)
    override def * = (ticketId, clientId) <> (TicketClientrEntity.tupled, TicketClientrEntity.unapply)
  }

  //JOINS 


  //CRUD


  //FILTERS 
  
}
