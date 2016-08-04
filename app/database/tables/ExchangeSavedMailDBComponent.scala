package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class ExchangeSavedMailEntity(
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

  class ExchangeMailTable(tag: Tag) extends Table[ExchangeSavedMailEntity](tag, "tbl_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def extId = column[String]("ext_id", O.SqlType("VARCHAR(255)"))
    def conversationExtId = column[String]("conversation_ext_id", O.SqlType("VARCHAR(255)"))
    def sender = column[String]("sender", O.SqlType("VARCHAR(255)"))
    def receivedBy = column[String]("received_by", O.SqlType("VARCHAR(255)"))
    def ccRecipients = column[String]("cc_recipients", O.SqlType("VARCHAR(255)"))
    def bccRecipients = column[String]("bcc_recipients", O.SqlType("VARCHAR(255)"))
    def subject = column[String]("subject", O.SqlType("VARCHAR(255)"))
    def body = column[String]("body")
    def importance = column[String]("importance", O.SqlType("VARCHAR(255)"))
    def attachments = column[String]("attachments", O.SqlType("VARCHAR(255)"))
    def size = column[Int]("size")
    def received = column[Timestamp]("received")

    def * = (id.?, extId, conversationExtId, sender, receivedBy, ccRecipients, bccRecipients,
             subject, body, importance, attachments, size, received) <> (ExchangeSavedMailEntity.tupled, ExchangeSavedMailEntity.unapply)

    def UqExtId = index("unique_saved_mail_ext_id", extId, unique = true)

  }

   def insertSavedMailEntity(mail: ExchangeSavedMailEntity): Future[ExchangeSavedMailEntity] = {
       db.run((saved_mails returning saved_mails.map(_.id)
                     into ((mail,id) => mail.copy(id=Some(id)))) += mail)
   }

  def getSavedMailEntityById(id: Int): Future[ExchangeSavedMailEntity] = {
       db.run(saved_mails.filter(_.id === id).result.head)
  }

  def getSavedMailEntityByExtId(extId: String): Future[ExchangeSavedMailEntity] = {
      db.run(saved_mails.filter(_.extId === extId).result.head)
  }

  def getSavedMailEntitiesByConversationId(converstionId: String): Future[List[ExchangeSavedMailEntity]] = {
      db.run(saved_mails.filter(_.conversationExtId === converstionId).sortBy(_.received.asc).result).map(_.toList)
  }

  def updateSavedMailEntity(mail: ExchangeSavedMailEntity): Future[ExchangeSavedMailEntity] = {
      db.run(saved_mails.filter(_.id === mail.id).update(mail))
                    .map( num => mail)
  }

  def deleteSavedMailEntityById(id: Int): Future[ExchangeSavedMailEntity] = {
      val deleted = getSavedMailEntityById(id)
      db.run(saved_mails.filter(_.id === id).delete)
      deleted
  }

  def deleteSavedMailEntityByExtId(extId: String): Future[ExchangeSavedMailEntity] = {
    val deleted = getSavedMailEntityByExtId(extId)
    db.run(saved_mails.filter(_.extId === extId).delete)
    deleted
  }

}

