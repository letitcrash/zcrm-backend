package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult
import utils.DBComponentWithSlickQueryOps

case class TicketActionEntity(
  id: Option[Int] = None,
  parentActionId: Option[Int] = None,
  ticketId: Int,
  userId: Int,
  actionType: Int,
  comment: Option[String] = None,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait TicketActionDBComponent extends DBComponentWithSlickQueryOps {
    this: DBComponent
    with UserDBComponent
    with TicketDBComponent =>

  import dbConfig.driver.api._

  val actions = TableQuery[ActionTable]

  class ActionTable(tag: Tag) extends Table[TicketActionEntity](tag, "tbl_ticket_action") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def parentActionId = column[Int]("parent_action_id", Nullable)
    def ticketId = column[Int]("ticket_id")
    def userId = column[Int]("user_id")
    def actionType = column[Int]("action_type")
    def comment = column[String]("comment", Nullable)
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null"))

    def fkParentActionId = foreignKey("fk_parent_action_id", parentActionId, actions)(_.id)
    def fkTicketId = foreignKey("fk_action_ticket_id", ticketId, tickets)(_.id)
    def fkUserId = foreignKey("fk_action_user_id", userId, users)(_.id)

    def * = (id.?, parentActionId.?, ticketId, userId, actionType, comment.?, recordStatus, createdAt, updatedAt)<>(TicketActionEntity.tupled, TicketActionEntity.unapply)
  }

  def actionsWithUsersWithProfile = actions join usersWithProfile on (_.userId === _._1.id)

  def actionQry(ticketId: Int, actionTypes: List[Int]) = {
    actionsWithUsersWithProfile.filter(a => (a._1.ticketId === ticketId && a._1.recordStatus === RowStatus.ACTIVE) )
           .filteredBy( actionTypes match { case List() => None; case list => Some(list) } )(_._1.actionType inSet _)
  }


  //CRUD ActionEntity
  def insertAction(action: TicketActionEntity): Future[TicketActionEntity] = {
      db.run((actions returning actions.map(_.id) into ((action,id) => action.copy(id=Some(id)))) += action)
  }

  def getActionEntityWithProfileById(id: Int): Future[(TicketActionEntity, (UserEntity, ContactProfileEntity))] = {
    db.run(actionsWithUsersWithProfile.filter(a =>(a._1.id === id &&
                                                   a._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getActionEntityById(id: Int): Future[TicketActionEntity] = {
   db.run(actions.filter(a =>(a.id === id &&
                              a.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateActionEntity(action: TicketActionEntity): Future[TicketActionEntity] = {
      db.run(actions.filter(a =>(a.id === action.id &&
                                 a.recordStatus === RowStatus.ACTIVE)).update(action)
                  .map{num => if(num != 0) action
                              else throw new Exception("Can't update action, is it deleted?")})
  }

  def softDeleteActionById(id: Int): Future[TicketActionEntity] = {
    getActionEntityById(id).flatMap(res =>
        updateActionEntity(res.copy(recordStatus = RowStatus.DELETED,
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  }

  //FILTERS
  def getActionEntitiesByTicketId(ticketId: Int, actionTypes: List[Int]): Future[List[(TicketActionEntity, (UserEntity, ContactProfileEntity))]] = {
    db.run(actionsWithUsersWithProfile.filter(a => (a._1.ticketId === ticketId &&
                                                    a._1.recordStatus === RowStatus.ACTIVE))
                  .filteredBy( actionTypes match { case List() => None; case list => Some(list) } )(_._1.actionType inSet _).result).map(_.toList)
  }

  def getActionEntitiesByUserId(userId: Int): Future[List[(TicketActionEntity, (UserEntity, ContactProfileEntity))]] = {
    db.run(actionsWithUsersWithProfile.filter(a => (a._1.userId === userId &&
                                                    a._1.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getActionEntitiesWithPagination(ticketId: Int, actionTypes: List[Int], pageSize: Int, pageNr: Int): Future[PagedDBResult[(TicketActionEntity, (UserEntity, ContactProfileEntity))]] = {
    val baseQry = actionQry(ticketId, actionTypes)    
    val pageRes = baseQry
      .sortBy(_._1.id.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( actionList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = actionList)
          )
        )
  }
}

