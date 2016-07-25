package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class TicketActionAttachedMailEntity(
  actionId: Int,
  mailId: Int)

trait TicketActionAttachedMailDBComponent extends DBComponent {
 this: DBComponent
 with SavedExchangeMailDBComponent
 with TicketActionDBComponent
 with TicketDBComponent =>

  import dbConfig.driver.api._

  val attachedMails = TableQuery[AttachedMailTable]

  class AttachedMailTable(tag: Tag) extends Table[TicketActionAttachedMailEntity](tag, "tbl_attached_mail") {
    def actionId = column[Int]("action_id")
    def mailId = column[Int]("mail_id")

    def fkActionId = foreignKey("fk_attached_mail_action_id", actionId, actions)(_.id)
    def fkMailtId = foreignKey("fk_attached_mail_saved_mail_id", mailId, saved_mails)(_.id)

    def * = (actionId, mailId) <> (TicketActionAttachedMailEntity.tupled, TicketActionAttachedMailEntity.unapply)
  }

  def attachedMailsWithMails = attachedMails join saved_mails on (_.mailId === _.id)

  //AttachedMailEntity CRUD
  def insertAttachedMailEnitity(attchedMail: TicketActionAttachedMailEntity): Future[TicketActionAttachedMailEntity] = {
    db.run(attachedMails += attchedMail).map( res => attchedMail)
  }

  def getAttachedMailWithMailEntityByActionId(actionId: Int): Future[(TicketActionAttachedMailEntity, ExchangeSavedMailEntity)] = {
    db.run(attachedMailsWithMails.filter(f => f._1.actionId === actionId).result.head)
  }

  def deleteAttachedMailEntity(entity: TicketActionAttachedMailEntity): Future[TicketActionAttachedMailEntity] = {
    db.run(attachedMails.filter( t => (t.actionId === entity.actionId &&
                                       t.mailId === entity.mailId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertAttachedMailEnitities(attchedMailList: List[TicketActionAttachedMailEntity]): Future[List[TicketActionAttachedMailEntity]] = {
    Future.sequence(attchedMailList.map( d =>  insertAttachedMailEnitity(d)))
  }

  def deleteAttachedMailEnitities(attchedMailList : List[TicketActionAttachedMailEntity]): Future[List[TicketActionAttachedMailEntity]] = {
    Future.sequence(attchedMailList.map( t =>  deleteAttachedMailEntity(t)))
    Future(attchedMailList)
  }

  def getAttachedMailsWithMailEntities(actionIds: List[Int]): Future[List[(TicketActionAttachedMailEntity, ExchangeSavedMailEntity)]] = {
    Future.sequence(actionIds.map(id => getAttachedMailWithMailEntityByActionId(id)))
  }

}

