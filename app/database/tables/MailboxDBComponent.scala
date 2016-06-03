package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._
import database.PagedDBResult

case class MailboxEntity(
  id: Option[Int] = None, 
  userId: Int,
  server: String,
  login: String,
  password: String, 
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait MailboxDBComponent extends DBComponent {
 this: DBComponent 
 with UserDBComponent =>

  import dbConfig.driver.api._

  val mailboxes = TableQuery[MailboxTable]

  class MailboxTable(tag: Tag) extends Table[MailboxEntity](tag, "tbl_mailbox") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def server = column[String]("server", O.SqlType("VARCHAR(255)"))
    def login = column[String]("login", O.SqlType("VARCHAR(255)"))
    def password = column[String]("password", O.SqlType("VARCHAR(255)"))
    def recordStatus = column[Int]("record_status",O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))

    def fkUserId = foreignKey("fk_mailbox_user", userId, users)(_.id)

    def * = (id.?, userId, server, login, password, recordStatus, createdAt, updatedAt) <> (MailboxEntity.tupled, MailboxEntity.unapply)
  }

  def mailboxQry(userId: Int) = {
    mailboxes.filter(m =>(m.userId === userId && 
                          m.recordStatus === RowStatus.ACTIVE))
  }

  //MailboxEntity CRUD

  def insertMailboxEnitity(mailbox: MailboxEntity): Future[MailboxEntity] = {
      db.run(((mailboxes returning mailboxes.map(_.id) 
                  into ((mailbox,id) => mailbox.copy(id=Some(id)))) += mailbox))
  } 

  def getMailboxEntityById(id:Int): Future[MailboxEntity] = {
      db.run(mailboxes.filter(m => (m.id === id &&
                                    m.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getMailboxEntitiesByUserId(userId: Int): Future[List[MailboxEntity]] = {
      db.run(mailboxes.filter(m => (m.userId === userId &&
                                    m.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
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

  def searchMailboxEntitiesByName(userId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[MailboxEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        mailboxQry(userId).filter{_.login.like(s)}
      }.getOrElse(mailboxQry(userId))  

    val pageRes = baseQry
      .sortBy(_.login.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( mailboxList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = mailboxList)
          )
        )
  }

}

