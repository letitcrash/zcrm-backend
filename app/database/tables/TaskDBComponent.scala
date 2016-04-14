package database.tables

import java.security.SecureRandom
import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{SetNull, Restrict}
import java.math.BigInteger
import java.security.SecureRandom
import models._


case class TaskEntity(
  id: Option[Int], 
  companyId: Int,
  createdByUserId: Int,
  assignedToUserId: Int,
  title: String,
  description: Option[String] = None,
  status: String = TaskStatus.NEW,
  attachedMailId: Option[Int],
  //TODO: should be Date()
  dueDate: Option[String],
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  //TODO: rename UserStatus -> RecrodStatus
  recordStatus: String = UserStatus.ACTIVE
  )

trait TaskDBComponent extends DBComponent
	with CompanyDBComponent
	with TaskAttachedMailDBComponent{
 this: DBComponent =>

  import dbConfig.driver.api._

	val tasks = TableQuery[TaskTable]

  class TaskTable(tag: Tag) extends Table[TaskEntity](tag, "tbl_task") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def createdByUserId = column[Int]("created_by_user_id")
    def assignedToUserId = column[Int]("assigned_to_user_id")
    def title = column[String]("title")
    def description = column[String]("description", Nullable)
    def status = column[String]("status",O.Default(TaskStatus.NEW))
    def attachedMailId = column[Int]("attached_mail_id", Nullable)
    def dueDate = column[String]("due_date_ts", Nullable)
    //TODO: check TS on DB
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def recordStatus = column[String]("record_status",O.Default(UserStatus.ACTIVE))
 
    //TODO: add FKs

    def fkCompanyId = foreignKey("fk_company_id", companyId, companies)(_.id, onUpdate = Restrict, onDelete = SetNull) 
    def fkAttachedMailId = foreignKey("fk_attached_mail_id", attachedMailId, taskAttachedMails)(_.id, onUpdate = Restrict, onDelete = SetNull) 

    def * = (id.?, companyId, createdByUserId, assignedToUserId, title, description.?, status, attachedMailId.?, dueDate.?, createdAt, updatedAt, recordStatus) <>(TaskEntity.tupled, TaskEntity.unapply)
  }

	//TaskEntity CRUD
	def insertTaskEntity(task: TaskEntity): Future[TaskEntity] = {
			db.run(((tasks returning tasks.map(_.id) 
								into ((task,id) => task.copy(id=Some(id)))) += task))
	}

	def getTaskEntityById(id: Int): Future[TaskEntity] = {
			db.run(tasks.filter(_.id === id).result.head)
	}

	def getTaskEntitiesByCompanyId(companyId: Int): Future[Seq[TaskEntity]] = {
			db.run(tasks.filter(_.companyId === companyId).result)
	}

	def updateTaskEntity(task: TaskEntity): Future[TaskEntity] = {
			db.run(tasks.filter(_.id === task.id).update(task))
        .map( num => task)
	}

	def softDeleteTaskEntityById(taskId: Int): Future[TaskEntity] = {
		getTaskEntityById(taskId).flatMap(res =>
			  updateTaskEntity(res.copy(status = UserStatus.DELETED, 
				   	                      updatedAt = new Timestamp(System.currentTimeMillis()))))

	}

	//TaskEntity Filters
  def upsertTaskEntity(task: TaskEntity): Future[TaskEntity] = {
    if(task.id.isDefined) {
      updateTaskEntity(task)
    } else {
      insertTaskEntity(task)
    }
  }
	
}

