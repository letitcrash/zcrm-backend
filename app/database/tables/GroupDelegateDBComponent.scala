package database.tables

import java.sql.Timestamp

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import slick.profile.SqlProfile.ColumnOption.Nullable

case class GroupDelegateEntity(
  delegateId: Option[Int],
  userId: Option[Int],
  delegateStartDate: Option[Timestamp],
  delegateEndDate: Option[Timestamp])

trait GroupDelegateDBComponent extends DBComponent {
  this: DBComponent
  with UserDBComponent 
  with DelegateDBComponent
  with ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val groupDelegates = TableQuery[GroupDelegateTable]

  class GroupDelegateTable(tag: Tag) extends Table[GroupDelegateEntity](tag, "tbl_group_delegate") {
    def delegateId = column[Int]("delegate_id")
    def userId = column[Int]("user_id")
    def delegateStartDate = column[Timestamp]("delegate_start_date", Nullable)
    def delegateEndDate = column[Timestamp]("delegate_end_date", Nullable)

    def fkUser = foreignKey("fk_group_delegate_user", userId, users)(_.id)
    def fkDelegate = foreignKey("fk_group_delegate_delegate", delegateId, delegates)(_.id)

    override def * = (delegateId.?, userId.?, delegateStartDate.?, delegateEndDate.?) <> (GroupDelegateEntity.tupled, GroupDelegateEntity.unapply)

  }

  // (GroupDelegateEntity, Delegate)
  def userWithDelegates = groupDelegates join delegates on (_.delegateId === _.id)

  //CRUD
  def insertGroupDelegate(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    //db.run((groupDelegates returning groupDelegates.map(_.delegateId) into ((gd, id) => gd.copy(delegateId = Some(id)))) += entity)
    db.run(groupDelegates += entity).map( res => entity)

  }

  //TODO: pointless 
  def getGroupDelegateEntityByDelegateId(delegateId: Int): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.delegateId === delegateId).result.head)
  }

  //TODO: pointless 
  def getGroupDelegateEntityByUserId(userId: Int): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.userId === userId).result.head)
  }

  //TODO: pointless 
  def updateGroupDelegateEntity(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.delegateId === entity.delegateId).update(entity).map { num =>
      if(num != 0) entity
      else throw new Exception("Can't update group delegate, is it deleted?")
    })
  }

  //TODO: pointless 
  def deleteGroupDelegateByDelegateId(delegateId: Int): Future[GroupDelegateEntity] = {
    val deleted = getGroupDelegateEntityByDelegateId(delegateId)
    db.run(groupDelegates.filter(_.delegateId === delegateId).delete)
    deleted
  }

  def deleteGroupDelegateByUserId(userId: Int): Future[GroupDelegateEntity] = {
    val lastDeleted = getGroupDelegateEntityByUserId(userId)
    db.run(groupDelegates.filter(_.userId === userId).delete)
    lastDeleted
  }

  def deleteGroupDelegate(groupDelegate : GroupDelegateEntity): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter( t => ( t.delegateId === groupDelegate.delegateId &&
                                  t.userId === groupDelegate.userId)).delete)
    Future(groupDelegate)
  }

  //FILTERS 
  def insertDelegateGroups(delegateGroups: List[GroupDelegateEntity]): Future[List[GroupDelegateEntity]] = {
    Future.sequence(delegateGroups.map( d =>  insertGroupDelegate(d)))
  }

  def getDelegateEntitiesByUserId(userId: Int): Future[List[(GroupDelegateEntity, DelegateEntity)]] = {
    db.run(userWithDelegates.filter( _._1.userId === userId).result).map(_.toList)
  }

  def deleteGroupDelegates(groupDelegates : List[GroupDelegateEntity]): Future[List[GroupDelegateEntity]] = {
    Future.sequence(groupDelegates.map( t =>  deleteGroupDelegate(t)))
    Future(groupDelegates)
  }

}
