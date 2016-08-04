package database.tables

import java.sql.Timestamp
import models.{UserLevels, RowStatus}
import slick.model.ForeignKeyAction.{Cascade, Restrict}
import scala.util.{Success, Failure, Try}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class FileFolderEntity(
  id: Option[Int] = None,
  parentId: Option[Int] = None,
  name: String,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()))


trait FileFolderDBComponent extends DBComponent {
  this: DBComponent =>

  import dbConfig.driver.api._

  val folders = TableQuery[FileFolderTable]
  
  class FileFolderTable(tag: Tag) extends Table[FileFolderEntity](tag, "tbl_file_folder") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def parentId = column[Int]("parent_id", Nullable)
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def createdAt = column[Timestamp]("created_at", Nullable)

    def fkParentFolderId = foreignKey("fk_parent_folder", parentId, folders)(_.id)

    def * = (id.?, parentId.?, name, createdAt)<>(FileFolderEntity.tupled, FileFolderEntity.unapply)
  }

  //FileFolderEntity CRUD
  def insertFileFolderEntity(file: FileFolderEntity): Future[FileFolderEntity] = {
    db.run((folders returning folders.map(_.id)
            into ((file,id) => file.copy(id=Some(id)))) += file)
  }

  def getFileFolderEntityById(id: Int): Future[FileFolderEntity] = {
    db.run(folders.filter(_.id === id).result.head)
  }

  def deleteFileFolderEntity(id: Int): Future[FileFolderEntity] = {
    val deletedFile = getFileFolderEntityById(id)
    db.run(folders.filter(_.id === id).delete)
    deletedFile
  }
}

