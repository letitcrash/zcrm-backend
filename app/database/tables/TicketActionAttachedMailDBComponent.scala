package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class TicketActionAttachedMailEntity(
  id: Option[Int] = None,
  actionId: Int,
  mailId: Int)

trait TicketActionAttachedMailDBComponent extends DBComponent {
 this: DBComponent
 with SavedExchangeMailDBComponent
 with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val attachedMails = TableQuery[AttachedMailTable]

  class AttachedMailTable(tag: Tag) extends Table[TicketActionAttachedMailEntity](tag, "tbl_attached_mail") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def actionId = column[Int]("action_id")
    def mailId = column[Int]("mail_id")

    def fkActionId = foreignKey("fk_attached_mail_action_id", actionId, actions)(_.id, onUpdate = Restrict, onDelete = Cascade)
    def fkMaiktId = foreignKey("fk_attached_mail_saved_mail_id", mailId, saved_mails)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, actionId, mailId) <> (TicketActionAttachedMailEntity.tupled, TicketActionAttachedMailEntity.unapply)
  }

  //AttachedMailEntity CRUD

  def insertAttachedMailEnitity(attchedMail: TicketActionAttachedMailEntity): Future[TicketActionAttachedMailEntity] = {
    db.run(((attachedMails returning attachedMails.map(_.id)
                  into ((attchedMail,id) => attchedMail.copy(id=Some(id)))) += attchedMail))
  }

  def getAttachedMailEntityById(id: Int): Future[TicketActionAttachedMailEntity] = {
    db.run(attachedMails.filter(_.id === id).result.head)
  }

  def deleteAttachedMailEntityById(id: Int): Future[TicketActionAttachedMailEntity] = {
    val deleted = getAttachedMailEntityById(id)
    db.run(attachedMails.filter(_.id === id).delete)
    deleted
  }

}

