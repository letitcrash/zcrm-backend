package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class MailboxEntity(
  id: Option[Int] = None, 
  userId: Int,
  server: String,
  login: String,
  password: String, 
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  status: String = RowStatus.ACTIVE)

trait MailboxDBComponent extends DBComponent {
 this: DBComponent 
 with UserDBComponent =>

  import dbConfig.driver.api._

	val mailboxes = TableQuery[MailboxTable]

  class MailboxTable(tag: Tag) extends Table[MailboxEntity](tag, "tbl_mailbox") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def server = column[String]("server")
    def login = column[String]("login")
    def password = column[String]("password")
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def recordStatus = column[String]("record_status",O.Default(RowStatus.ACTIVE))

    def fkUserId = foreignKey("fk_mailbox_user", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade ) 

    def * = (id.?, userId, server, login, password, createdAt, updatedAt, recordStatus) <> (MailboxEntity.tupled, MailboxEntity.unapply)
  }
	
}

