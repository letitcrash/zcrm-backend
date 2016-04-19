package database.tables

import java.sql.Timestamp
import models.{UserLevels, RowStatus}
import slick.model.ForeignKeyAction.{Cascade, Restrict}
import scala.util.{Success, Failure, Try}
import exceptions.UsernameAlreadyExistException
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.mindrot.jbcrypt.BCrypt

case class FileEntity(
  id: Option[Int] = None,
  userId: Int,
  hash: String,
  filename: String,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()))


trait FileDBComponent extends DBComponent {
  this: DBComponent 
    with UserDBComponent
    with ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val files = TableQuery[FileTable]
  
  class FileTable(tag: Tag) extends Table[FileEntity](tag, "tbl_files") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("user_id")
    def fileHash = column[String]("file_hash")
    def fileName = column[String]("file_name")    
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))


    def fkUser = foreignKey("fk_file_user", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, userId, fileHash, fileName, createdAt) <>(FileEntity.tupled, FileEntity.unapply)
  }

  def filesWithUsersWihtProfile = files join usersWithProfile on (_.userId === _._1.id)

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

}

