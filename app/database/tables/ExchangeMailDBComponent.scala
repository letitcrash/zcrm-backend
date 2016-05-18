package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class ExchangeMailEntity(
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

trait ExchangeMailDBComponent extends DBComponent {
 this: DBComponent  =>

  import dbConfig.driver.api._

  val mails = TableQuery[ExchangeMailTable]

  class ExchangeMailTable(tag: Tag) extends Table[ExchangeMailEntity](tag, "tbl_ods_mails") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
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

    def * = (id.?, extId, conversationExtId, sender, receivedBy, ccRecipients, bccRecipients, 
						 subject, body, importance, attachments, size, received) <> (ExchangeMailEntity.tupled, ExchangeMailEntity.unapply)
  }

}

