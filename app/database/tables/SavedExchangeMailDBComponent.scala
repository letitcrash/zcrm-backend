package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class SavedExchangeMailEntity(
  id: Option[Int] = None,
  extId: String,
  conversationExtId: String,
  sender: String,
  receivedBy: String,
  ccRecipients: String,
  bccRecipients: String,
  subject: String,
  body: String,
  importance: String,
  attachments: String,
  size: Int,
  received: Timestamp)

trait SavedExchangeMailDBComponent extends DBComponent {
 this: DBComponent =>

  import dbConfig.driver.api._

  val saved_mails = TableQuery[ExchangeMailTable]

  class ExchangeMailTable(tag: Tag) extends Table[SavedExchangeMailEntity](tag, "tbl_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def extId = column[String]("ext_id", O.SqlType("VARCHAR(255)"))
    def conversationExtId = column[String]("conversation_ext_id")
    def sender = column[String]("sender")
    def receivedBy = column[String]("received_by")
    def ccRecipients = column[String]("cc_recipients")
    def bccRecipients = column[String]("bcc_recipients")
    def subject = column[String]("subject")
    def body = column[String]("body")
    def importance = column[String]("importance")
    def attachments = column[String]("attachments")
    def size = column[Int]("size")
    def received = column[Timestamp]("received")

    def * = (id.?, extId, conversationExtId, sender, receivedBy, ccRecipients, bccRecipients,
             subject, body, importance, attachments, size, received) <> (SavedExchangeMailEntity.tupled, SavedExchangeMailEntity.unapply)

    def UqExtId = index("unique_saved_mail_ext_id", extId, unique = true)

  }

   def insertMailEntity(mail: SavedExchangeMailEntity): Future[SavedExchangeMailEntity] = {
       db.run((saved_mails returning saved_mails.map(_.id)
                     into ((mail,id) => mail.copy(id=Some(id)))) += mail)
   }

  def getMailEntityById(id: Int): Future[SavedExchangeMailEntity] = {
       db.run(saved_mails.filter(_.id === id).result.head)
  }

  def getMailEntityByExtId(extId: String): Future[SavedExchangeMailEntity] = {
      db.run(saved_mails.filter(_.extId === extId).result.head)
  }

  def getMailEntitiesByConversationId(converstionId: String): Future[List[SavedExchangeMailEntity]] = {
      db.run(saved_mails.filter(_.conversationExtId === converstionId).result).map(_.toList)
  }

  def updateMailEntity(mail: SavedExchangeMailEntity): Future[SavedExchangeMailEntity] = {
      db.run(saved_mails.filter(_.id === mail.id).update(mail))
                    .map( num => mail)
  }

  def deleteMailEntityById(id: Int): Future[SavedExchangeMailEntity] = {
      val deleted = getMailEntityById(id)
      db.run(saved_mails.filter(_.id === id).delete)
      deleted
  }

  def deleteMailEntityByExtId(extId: String): Future[SavedExchangeMailEntity] = {
    val deleted = getMailEntityByExtId(extId)
    db.run(saved_mails.filter(_.extId === extId).delete)
    deleted
  }

}

