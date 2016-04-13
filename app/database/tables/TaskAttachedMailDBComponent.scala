package database.tables

import java.security.SecureRandom
import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.math.BigInteger
import java.security.SecureRandom


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


}

