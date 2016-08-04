package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class TicketTeamMemberEntity(
  ticketId: Int,
  teamId: Int)

trait TicketTeamMemberDBComponent extends DBComponent { 
  this: DBComponent 
  with TicketDBComponent
  with TeamDBComponent => 

  import dbConfig.driver.api._

  val ticketTeamMembers = TableQuery[TicketTeamMemberTable]

  class TicketTeamMemberTable(tag: Tag) extends Table[TicketTeamMemberEntity](tag, "tbl_ticket_team_member") {
    def ticketId = column[Int]("ticket_id")
    def teamId = column[Int]("team_id")

    def fkTicket = foreignKey("fk_ticket_team_member_ticket", ticketId, tickets)(_.id)
    def fkTeam = foreignKey("fk_ticket_team_member_team", teamId, teams)(_.id)
    override def * = (ticketId, teamId) <> (TicketTeamMemberEntity.tupled, TicketTeamMemberEntity.unapply)
  }
  
  //JOINS 

  //(TicketTeamMemberEntity , TeamEntity)
  def ticketTeamMembersWithTeam = ticketTeamMembers join teams on (_.teamId === _.id) 

  //CRUD
  def insertTicketTeamMember(entity: TicketTeamMemberEntity): Future[TicketTeamMemberEntity] = {
    db.run(ticketTeamMembers += entity).map( res => entity)
  }

  def deleteTicketTeamMember(entity: TicketTeamMemberEntity): Future[TicketTeamMemberEntity] = {
    db.run(ticketTeamMembers.filter( t => (t.ticketId === entity.ticketId &&
                                           t.teamId === entity.teamId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertTicketTeamMembers(ticketTeamMemberList: List[TicketTeamMemberEntity]): Future[List[TicketTeamMemberEntity]] = {
    Future.sequence(ticketTeamMemberList.map( d =>  insertTicketTeamMember(d)))
  }

  def deleteTicketTeamMembers(ticketTeamMemberList : List[TicketTeamMemberEntity]): Future[List[TicketTeamMemberEntity]] = {
    Future.sequence(ticketTeamMemberList.map( t =>  deleteTicketTeamMember(t)))
    Future(ticketTeamMemberList)
  }

  def deleteAllTeamsByTicketId(ticketId: Int): Future[Int] = {
    db.run(ticketTeamMembers.filter(t => ( t.ticketId === ticketId)).delete)
  }

  def getTeamsByTicketId(ticketId: Int): Future[List[(TicketTeamMemberEntity , TeamEntity)]] = {
    db.run(ticketTeamMembersWithTeam.filter(_._1.ticketId === ticketId ).result).map(_.toList)
  }
  

}
