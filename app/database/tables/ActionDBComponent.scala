package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable

case class ActionEntity(
  id: Option[Int] = None,
  parentActionId: Option[Int] = None,
  ticketId: Int,
  userId: Int,
  actionType: Int,
  name: String,
  comment: Option[String] = None,
  recordStatus: String = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait ActionDBComponent extends DBComponent {
    this: DBComponent
    with UserDBComponent => 

  import dbConfig.driver.api._

  val actions = TableQuery[ActionTable]

  class ActionTable(tag: Tag) extends Table[ActionEntity](tag, "tbl_action") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def parentActionId = column[Int]("company_id", Nullable)
    def ticketId = column[Int]("ticket_id")
    def userId = column[Int]("user_id")
    def actionType = column[Int]("action_type")
    def name = column[String]("name")
    def comment = column[String]("comment", Nullable)
    def recordStatus = column[String]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))

    def fkParentActionId = foreignKey("fk_parent_action", parentActionId, actions)(_.id, onUpdate = Restrict, onDelete = Cascade)
    //def fkTicketId = foreignKey("fk_action_ticket", ticketId, tickets)(_.id, onUpdate = Restrict, onDelete = Cascade)
    def fkUserId = foreignKey("fk_action_user", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, parentActionId, ticketId, userId, actionType, comment, recordStatus, createdAt, updatedAt)<>(ActionEntity.tupled, ActionEntity.unapply)
  }

  //CRUD ActionEntity
  def insertAction(action: ActionEntity): Future[ActionEntity] = {
      db.run((actions returning actions.map(_.id) into ((action,id) => action.copy(id=Some(id)))) += action)
  }

  def getActionEntityById(id: Int): Future[ActionEntity] = {
    db.run(actions.filter(a =>(a.id === id &&
                                         a.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateActionEntity(action: ActionEntity): Future[ActionEntity] = {
      db.run(actions.filter(a =>(a.id === action.id &&
                                           a.recordStatus === RowStatus.ACTIVE)).update(action)
                  .map{num => if(num != 0) action 
                              else throw new Exception("Can't update action, is it deleted?")})
  }

  def softDeleteActionById(id: Int): Future[ActionEntity] = {
    getActionEntityById(id).flatMap(res =>
        updateActionEntity(res.copy(recordStatus = RowStatus.DELETED, 
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getActionEntitiesByTicketId(ticketId: Int): Future[List[ActionEntity]] = {
    db.run(actions.filter(a => (a.ticketId === ticketId &&
                                                   a.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getActionEntitiesByUserId(userId: Int): Future[List[ActionEntity]] = {
    db.run(actions.filter(a => (a.userId === userId &&
                                         a.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }
}

