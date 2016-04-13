package database.tables

import java.security.SecureRandom
import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
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

trait TaskDBComponent extends DBComponent{
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

    def * = (id.?, companyId, createdByUserId, assignedToUserId, title, description.?, status, attachedMailId.?, dueDate.?, createdAt, updatedAt, recordStatus) <>(TaskEntity.tupled, TaskEntity.unapply)
  }


}

