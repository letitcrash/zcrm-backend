package database.tables

import java.sql.Timestamp

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import slick.profile.SqlProfile.ColumnOption.Nullable

case class GroupDelegateEntity(
  id: Option[Int],
  userId: Int,
  delegateStartDate: Option[Timestamp],
  delegateEndDate: Option[Timestamp])

trait GroupDelegateDBComponent extends DBComponent
  with UserDBComponent 
  with DelegateDBComponent {
  this: DBComponent with ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val groupDelegates = TableQuery[GroupDelegateTable]

  class GroupDelegateTable(tag: Tag) extends Table[GroupDelegateEntity](tag, "tbl_group_delegate") {
    def delegateId = column[Int]("delegate_id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def delegateStartDate = column[Timestamp]("delegate_start_date", Nullable)
    def delegateEndDate = column[Timestamp]("delegate_end_date", Nullable)

    def fkUser = foreignKey("fk_group_delegate_user", userId, users)(_.id)
    def fkDelegate = foreignKey("fk_group_delegate_delegate", delegateId, delegates)(_.id)

    override def * = (delegateId.?, userId, delegateStartDate.?, delegateEndDate.?) <> (GroupDelegateEntity.tupled, GroupDelegateEntity.unapply)

  }

  // (GroupDelegateEntity, Delegate)
  def userWithDelegates = groupDelegates join delegates on (_.delegateId === _.id)

  //CRUD
  def insertGroupDelegate(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    val delegateStartDate = Some(new Timestamp(System.currentTimeMillis()))

    db.run((groupDelegates returning groupDelegates.map(_.delegateId) into ((gd, id) => gd.copy(id = Some(id))))
      += entity.copy(delegateStartDate = delegateStartDate))
  }

  def getGroupDelegateEntityById(id: Int): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.delegateId === id).result.head)
  }

  def updateGroupDelegateEntity(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.delegateId === entity.id).update(entity).map { num =>
      if(num != 0) entity
      else throw new Exception("Can't update group delegate, is it deleted?")
    })
  }

  def deleteGroupDelegateById(id: Int): Future[GroupDelegateEntity] = {
    val deleted = getGroupDelegateEntityById(id)
    db.run(groupDelegates.filter(_.delegateId === id).delete)
    deleted
  }

  //FILTERS 
  def getDelegateEntitiesByUserId(userId: Int): Future[List[(GroupDelegateEntity, DelegateEntity)]] = {
    db.run(userWithDelegates.filter( _._1.userId === userId).result).map(_.toList)
  }

}
