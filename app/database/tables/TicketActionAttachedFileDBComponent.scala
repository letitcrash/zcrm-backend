package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class TicketActionAttachedFileEntity(
  id: Option[Int] = None,
  actionId: Int,
  fileId: Int)

trait TicketActionAttachedFileDBComponent extends DBComponent {
 this: DBComponent
 with FileDBComponent
 with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val attachedFiles = TableQuery[AttachedFileTable]

  class AttachedFileTable(tag: Tag) extends Table[TicketActionAttachedFileEntity](tag, "tbl_attached_file") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def actionId = column[Int]("action_id")
    def fileId = column[Int]("file_id")

    def fkActionId = foreignKey("fk_attached_file_action_id", actionId, actions)(_.id)
    def fkFiletId = foreignKey("fk_attached_file_saved_file_id", fileId, files)(_.id)

    def * = (id.?, actionId, fileId) <> (TicketActionAttachedFileEntity.tupled, TicketActionAttachedFileEntity.unapply)
  }

  //AttachedFileEntity CRUD

  def insertAttachedFileEnitity(attchedFile: TicketActionAttachedFileEntity): Future[TicketActionAttachedFileEntity] = {
    db.run(((attachedFiles returning attachedFiles.map(_.id)
                  into ((attchedFile,id) => attchedFile.copy(id=Some(id)))) += attchedFile))
  }

  def getAttachedFileEntityById(id: Int): Future[TicketActionAttachedFileEntity] = {
    db.run(attachedFiles.filter(_.id === id).result.head)
  }

  def deleteAttachedFileEntityById(id: Int): Future[TicketActionAttachedFileEntity] = {
    val deleted = getAttachedFileEntityById(id)
    db.run(attachedFiles.filter(_.id === id).delete)
    deleted
  }

}

