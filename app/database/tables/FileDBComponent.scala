package database.tables

import java.sql.Timestamp
import models.{UserLevels, RowStatus}
import slick.model.ForeignKeyAction.{Cascade, Restrict}
import scala.util.{Success, Failure, Try}
import exceptions.UsernameAlreadyExistException
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.mindrot.jbcrypt.BCrypt
import database.PagedDBResult
import slick.profile.SqlProfile.ColumnOption.Nullable

case class FileEntity(
  id: Option[Int] = None,
  userId: Int,
  hash: String,
  filename: String,
  folderId: Option[Int] = None,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()))


trait FileDBComponent extends DBComponent {
  this: DBComponent 
    with UserDBComponent
    with ContactProfileDBComponent
    with FileFolderDBComponent =>

  import dbConfig.driver.api._

  val files = TableQuery[FileTable]
  
  class FileTable(tag: Tag) extends Table[FileEntity](tag, "tbl_files") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def fileHash = column[String]("file_hash", O.SqlType("VARCHAR(255)"))
    def fileName = column[String]("file_name", O.SqlType("VARCHAR(255)"))    
    def folderId = column[Int]("folder", Nullable)    
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))


    def fkUser = foreignKey("fk_file_user", userId, users)(_.id)
    def fkFolder = foreignKey("fk_file_folder", folderId, folders)(_.id)

    def * = (id.?, userId, fileHash, fileName, folderId.?, createdAt) <>(FileEntity.tupled, FileEntity.unapply)
  }

  def filesWithUsersWihtProfile = files join usersWithProfile on (_.userId === _._1.id)

  def fileQry(userId: Int) = {
    files.filter(_.userId === userId) 
  }

  //FileEntity CRUD
  def insertFileEntity(file: FileEntity): Future[FileEntity] = {
    db.run((files returning files.map(_.id)
                  into ((file,id) => file.copy(id=Some(id)))) += file)
  }

  def getFileEntityById(id: Int): Future[FileEntity] = {
    db.run(files.filter(_.id === id).result.head)
  }

  def getFileEntitiesByUserId(userId: Int): Future[List[FileEntity]] = {
    db.run(files.filter(_.userId === userId).result).map(_.toList)
  }

  def deleteFileEntity(id: Int): Future[FileEntity] = {
    val deletedFile = getFileEntityById(id)
    db.run(files.filter(_.id === id).delete)
    deletedFile
  }


  //FILTERS
  def searchFileEntitiesByName(userId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[FileEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        fileQry(userId).filter{_.fileName.like(s)}
      }.getOrElse(fileQry(userId))  

    val pageRes = baseQry
      .sortBy(_.fileName.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( fileList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = fileList)
          )
        )
  }
}

