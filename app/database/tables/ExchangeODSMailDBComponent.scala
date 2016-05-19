package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class ExchangeODSMailEntity(
  id: Option[Int] = None,
  mailboxId: Int,
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

trait ExchangeODSMailDBComponent extends DBComponent {
 this: DBComponent
 with MailboxDBComponent=>

  import dbConfig.driver.api._

  val ods_mails = TableQuery[ExchangeMailTable]

  class ExchangeMailTable(tag: Tag) extends Table[ExchangeODSMailEntity](tag, "tbl_ods_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def mailboxId = column[Int]("mailbox_id")
    def extId = column[String]("ext_id")
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

    def mailboxIdFk = foreignKey("fk_ods_mailbox_id", mailboxId, mailboxes)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, mailboxId, extId, conversationExtId, sender, receivedBy, ccRecipients, bccRecipients,
             subject, body, importance, attachments, size, received) <> (ExchangeODSMailEntity.tupled, ExchangeODSMailEntity.unapply)
  }

   def insertODSMailEntity(mail: ExchangeODSMailEntity): Future[ExchangeODSMailEntity] = {
       db.run((ods_mails returning ods_mails.map(_.id)
                     into ((mail,id) => mail.copy(id=Some(id)))) += mail)
   }

  def getODSMailEntityById(id: Int): Future[ExchangeODSMailEntity] = {
       db.run(ods_mails.filter(_.id === id).result.head)
  }

  def getODSMailEntityByExtId(extId: String): Future[ExchangeODSMailEntity] = {
      db.run(ods_mails.filter(_.extId === extId).result.head)
  }

  def getODSMailEntitiesByConversationId(converstionId: String): Future[List[ExchangeODSMailEntity]] = {
      db.run(ods_mails.filter(_.conversationExtId === converstionId).result).map(_.toList)
  }

  def getODSMailEntitiesByMailboxId(mailboxId: Int): Future[List[ExchangeODSMailEntity]] = {
      db.run(ods_mails.filter(_.mailboxId === mailboxId).result).map(_.toList)
  }

  def updateODSMailEntity(mail: ExchangeODSMailEntity): Future[ExchangeODSMailEntity] = {
      db.run(ods_mails.filter(_.id === mail.id).update(mail))
                    .map( num => mail)
  }

  def deleteODSMailEntityById(id: Int): Future[ExchangeODSMailEntity] = {
      val deleted = getODSMailEntityById(id)
      db.run(ods_mails.filter(_.id === id).delete)
      deleted
  }

  def deleteODSMailEntityByExtId(extId: String): Future[ExchangeODSMailEntity] = {
    val deleted = getODSMailEntityByExtId(extId)
    db.run(ods_mails.filter(_.extId === extId).delete)
    deleted
  }

}

