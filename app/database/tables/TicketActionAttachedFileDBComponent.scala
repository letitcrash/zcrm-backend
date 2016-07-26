package database.tables

import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.model.ForeignKeyAction.{Cascade, SetNull, Restrict}
import models._

case class TicketActionAttachedFileEntity(
  actionId: Int,
  fileId: Int)

trait TicketActionAttachedFileDBComponent extends DBComponent {
 this: DBComponent
 with FileDBComponent
 with TicketActionDBComponent =>

  import dbConfig.driver.api._

  val attachedFiles = TableQuery[AttachedFileTable]

  class AttachedFileTable(tag: Tag) extends Table[TicketActionAttachedFileEntity](tag, "tbl_attached_file") {
    def actionId = column[Int]("action_id")
    def fileId = column[Int]("file_id")

    def fkActionId = foreignKey("fk_attached_file_action_id", actionId, actions)(_.id)
    def fkFiletId = foreignKey("fk_attached_file_saved_file_id", fileId, files)(_.id)

    def * = (actionId, fileId) <> (TicketActionAttachedFileEntity.tupled, TicketActionAttachedFileEntity.unapply)
  }

  def attachedFilesWithFiles = attachedFiles join files on (_.fileId === _.id)

  //AttachedFileEntity CRUD
  def insertAttachedFileEnitity(attchedFile: TicketActionAttachedFileEntity): Future[TicketActionAttachedFileEntity] = {
    db.run(attachedFiles += attchedFile).map( res => attchedFile)
  }

  def getAttachedFileWithFileEntityByActionId(actionId: Int): Future[List[(TicketActionAttachedFileEntity, FileEntity)]] = {
    db.run(attachedFilesWithFiles.filter(f => f._1.actionId === actionId).result).map(_.toList)
  }

  def deleteAttachedFileEntity(entity: TicketActionAttachedFileEntity): Future[TicketActionAttachedFileEntity] = {
    db.run(attachedFiles.filter( t => (t.actionId === entity.actionId &&
                                       t.fileId === entity.fileId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertAttachedFileEnitities(attchedFileList: List[TicketActionAttachedFileEntity]): Future[List[TicketActionAttachedFileEntity]] = {
    Future.sequence(attchedFileList.map( d =>  insertAttachedFileEnitity(d)))
  }

  def deleteAttachedFileEnitities(attchedFileList : List[TicketActionAttachedFileEntity]): Future[List[TicketActionAttachedFileEntity]] = {
    Future.sequence(attchedFileList.map( t =>  deleteAttachedFileEntity(t)))
    Future(attchedFileList)
  }

/*  def getAttachedFilesWithFileEntities(actionIds: List[Int]): Future[List[(TicketActionAttachedFileEntity, FileEntity)]] = {
    Future.sequence(actionIds.map(id => getAttachedFileWithFileEntityByActionId(id)))
  }
*/
}

