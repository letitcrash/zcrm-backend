package database


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.UploadedFile
import play.api.Logger
import java.io.File
import database.tables.FileEntity
import play.api.mvc.MultipartFormData._

object FileRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

	def saveFile(uploadedFile: UploadedFile): Future[UploadedFile] = {
		import utils.converters.FileConverter._
		insertFileEntity(uploadedFile.asFileEntity).map(savedFile => savedFile.asUploadedFile)
	}
  

}
