package database_rf

import java.net.URI
import java.sql.Date
import java.sql.Timestamp

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.concurrent.Future

import database.ExchangeODSMailDBRepository
import database.tables.ExchangeODSMailEntity
import javax.inject.Singleton
import microsoft.exchange.webservices.data.core.ExchangeService
import microsoft.exchange.webservices.data.core.PropertySet
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName
import microsoft.exchange.webservices.data.core.service.item.EmailMessage
import microsoft.exchange.webservices.data.credential.WebCredentials
import microsoft.exchange.webservices.data.property.complex.FolderId
import models.MailboxRf
import play.api.libs.json.{Json, JsValue}

class MailboxesDBComponent(val db: Database) {
  import db.config.driver.api._

  class Mailboxes(tag: Tag) extends Table[MailboxRf](tag, "tbl_mailbox") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def server = column[String]("server")
    def login = column[String]("login")
    def password = column[String]("password")
    def created = column[Option[Date]]("created_at")
    def updated = column[Option[Date]]("updated_at")
    def syncState = column[String]("sync_state")
    
    def * = (id, userId, server, login, password, created, updated, syncState) <>
      (MailboxRf.tupled, MailboxRf.unapply)
  }
  
  val mailboxes = TableQuery[Mailboxes]
  
  def count(userId: Int): Future[Int] = {
    db.instance.run(mailboxes.filter(_.userId === userId).countDistinct.result)
  }

  def get(userId: Int): Future[Seq[MailboxRf]] = {
    db.instance.run(mailboxes.filter(_.userId === userId).result)
  }
  
  def synchronize(mailbox: MailboxRf): JsValue = {
    val service = new ExchangeService()
    service.setCredentials(new WebCredentials(mailbox.login, mailbox.password))
    service.setUrl(new URI(mailbox.server))
    
    val syncResponse = service.endSyncFolderItems(service.beginSyncFolderItems(
        null,
        null,
        new FolderId(WellKnownFolderName.Inbox),
        PropertySet.FirstClassProperties,
        null,
        512,
        null,
        mailbox.syncState))

    ExchangeODSMailDBRepository.insertEmails(syncResponse.map { item =>
      val message = EmailMessage.bind(service, item.getItemId)

      ExchangeODSMailEntity(
          None,
          mailbox.id,
          message.getId.toString,
          message.getConversationId.toString,
          message.getFrom.getAddress,
          message.getReceivedBy.getAddress,
          message.getCcRecipients.mkString(","),
          message.getBccRecipients.mkString(","),
          message.getSubject,
          message.getBody.toString,
          message.getImportance.toString,
          "Nothing",
          message.getSize,
          Timestamp.from(message.getDateTimeReceived.toInstant))
    })

    val count = db.instance.run(mailboxes
        .filter(_.id === mailbox.id)
        .map(_.syncState)
        .update(syncResponse.getSyncState))
    
    Json.parse(s"""{"email":"${mailbox.login}","count":${count}}""")
  }
}

object MailboxesDBComponent {
  def apply(db: Database) = new MailboxesDBComponent(db)
}