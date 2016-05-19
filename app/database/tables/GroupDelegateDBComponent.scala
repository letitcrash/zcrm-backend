package database.tables

import java.sql.Timestamp

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class GroupDelegateEntity(
  id: Option[Int],
  userId: Int,
  delegateStartDate: Timestamp,
  delegateEndDate: Timestamp)

trait GroupDelegateDBComponent extends DBComponent
  with UserDBComponent {
  this: DBComponent with ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val groupDelegates = TableQuery[GroupDelegateTable]

  class GroupDelegateTable(tag: Tag) extends Table[GroupDelegateEntity](tag, "tbl_group_delegate") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def delegateStartDate = column[Timestamp]("delegate_start_date")
    def delegateEndDate = column[Timestamp]("delegate_end_date")

    def fkUser = foreignKey("fk_group_delegate_user", userId, users)(_.id)

    override def * = (id.?, userId, delegateStartDate, delegateEndDate) <> (GroupDelegateEntity.tupled, GroupDelegateEntity.unapply)

  }

  def insertGroupDelegate(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    val delegateStartDate = new Timestamp(System.currentTimeMillis())

    db.run((groupDelegates returning groupDelegates.map(_.id) into ((gd, id) => gd.copy(id = Some(id))))
      += entity.copy(delegateStartDate = delegateStartDate))
  }

  def getGroupDelegateEntityById(id: Int): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.id === id).result.head)
  }

  def updateGroupDelegateEntity(entity: GroupDelegateEntity): Future[GroupDelegateEntity] = {
    db.run(groupDelegates.filter(_.id === entity.id).update(entity).map { num =>
      if(num != 0) entity
      else throw new Exception("Can't update group delegate, is it deleted?")
    })
  }

  def deleteGroupDelegateById(id: Int): Future[GroupDelegateEntity] = {
    val deleted = getGroupDelegateEntityById(id)
    db.run(groupDelegates.filter(_.id === id).delete)
    deleted
  }

}
