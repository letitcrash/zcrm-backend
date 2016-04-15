package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}

case class TaskAttachedMailEntity(
  id: Option[Int] = None, 
  taskId: Int, 
  mailExtId: String,
  from: String, 
  subject: Option[String] = None
  )


trait TaskAttachedMailDBComponent extends DBComponent
 with TaskDBComponent {
 this: DBComponent =>

  import dbConfig.driver.api._

	val taskAttachedMails = TableQuery[TaskAttachedMailTable]

  //TODO: add fild/FK to tbl_task
  class TaskAttachedMailTable(tag: Tag) extends Table[TaskAttachedMailEntity](tag, "tbl_task_attached_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def taskId = column[Int]("task_id")
    def mailExtId = column[String]("mail_ext_id")
    def from = column[String]("mail_from")
    def subject = column[String]("mail_subject")

    def fkTaskId = foreignKey("fk_attachedtask_task", taskId, tasks)(_.id, onUpdate = Restrict, onDelete = Cascade ) 

    def * = (id.?, taskId, mailExtId, from, subject.?) <>(TaskAttachedMailEntity.tupled, TaskAttachedMailEntity.unapply)

  }
	

	//TaskAttachedMailEntity CRUD
	def insertAttachedMailEntity(mailEntity: TaskAttachedMailEntity): Future[TaskAttachedMailEntity] = {
			db.run(((taskAttachedMails returning taskAttachedMails.map(_.id) 
								into ((mailEntity,id) => mailEntity.copy(id=Some(id)))) += mailEntity))
	}

	def getAttachedMailEntityById(id: Int): Future[TaskAttachedMailEntity] = {
			db.run(taskAttachedMails.filter(_.id === id).result.head)
	}

  
	def getAttachedMailEntityByTaskId(taskId: Int): Future[List[TaskAttachedMailEntity]] = {
			db.run(taskAttachedMails.filter(_.taskId === taskId).result).map(_.toList)
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


  def insertAttachedMailEntities(mailEntities: List[TaskAttachedMailEntity]): Future[List[TaskAttachedMailEntity]] = {
    mailEntities.map( mailEntt => insertAttachedMailEntity(mailEntt))
    getAttachedMailEntityByTaskId(mailEntities.head.taskId)
  }

}

