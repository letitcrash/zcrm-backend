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
  recordStatus: String = RowStatus.ACTIVE)

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

  //MailboxEntity CRUD

  def insertMailboxEnitity(mailbox: MailboxEntity): Future[MailboxEntity] = {
      db.run(((mailboxes returning mailboxes.map(_.id) 
                  into ((mailbox,id) => mailbox.copy(id=Some(id)))) += mailbox))
  } 

  def getMailboxEntityById(id:Int): Future[MailboxEntity] = {
      db.run(mailboxes.filter(t => (t.id === id &&
                                    t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getMailboxEntitiesByUserId(userId: Int): Future[List[MailboxEntity]] = {
      db.run(mailboxes.filter(t => (t.userId === userId &&
                                    t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def updateMailboxEntity(mailbox: MailboxEntity): Future[MailboxEntity] = {
     db.run(mailboxes.filter(_.id === mailbox.id).update(mailbox))
        .map( num => mailbox)

  }

  def softDeleteMailboxEntityById(id: Int): Future[MailboxEntity] = {
      getMailboxEntityById(id).flatMap(res =>
          updateMailboxEntity(res.copy(recordStatus = RowStatus.DELETED, 
                                 updatedAt = new Timestamp(System.currentTimeMillis()))))
  }

}

