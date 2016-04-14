package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


case class TaskAttachedMailEntity(
  id: Option[Int], 
  mailExtId: String,
  from: String, 
  subject: Option[String] = None
  )


trait TaskAttachedMailDBComponent extends DBComponent{
 this: DBComponent =>

  import dbConfig.driver.api._

	val taskAttachedMails = TableQuery[TaskAttachedMailTable]

  class TaskAttachedMailTable(tag: Tag) extends Table[TaskAttachedMailEntity](tag, "tbl_task_attached_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def mailExtId = column[String]("mail_ext_id")
    def from = column[String]("mail_from")
    def subject = column[String]("mail_subject")

    def * = (id.?, mailExtId, from, subject.?) <>(TaskAttachedMailEntity.tupled, TaskAttachedMailEntity.unapply)

  }
	

	//TaskAttachedMailEntity CRUD
	def insertAttachedMailEntity(mailEntity: TaskAttachedMailEntity): Future[TaskAttachedMailEntity] = {
			db.run(((taskAttachedMails returning taskAttachedMails.map(_.id) 
								into ((mailEntity,id) => mailEntity.copy(id=Some(id)))) += mailEntity))
	}

	def getAttachedMailEntityById(id: Int): Future[TaskAttachedMailEntity] = {
			db.run(taskAttachedMails.filter(_.id === id).result.head)
	}

	def getAttachedMailEntityByMailId(mailId: String): Future[TaskAttachedMailEntity] = {
			db.run(taskAttachedMails.filter(_.mailExtId === mailId).result.head)
	}

  def updateAttachedMailEntity(mailEntity: TaskAttachedMailEntity): Future[TaskAttachedMailEntity] = {
      db.run(taskAttachedMails.filter(_.id === mailEntity.id).update(mailEntity))
                  .map( num => mailEntity)
  }



	//TaskAttachedMailEntity Filters
  def upsertAttachedMailEntity(mailEntity: TaskAttachedMailEntity): Future[TaskAttachedMailEntity] = {
    if(mailEntity.id.isDefined) {
      updateAttachedMailEntity(mailEntity)
    } else {
      insertAttachedMailEntity(mailEntity)
    }
  }
}

