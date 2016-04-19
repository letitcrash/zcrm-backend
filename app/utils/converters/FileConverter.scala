package utils.converters
import models._
import database.tables.FileEntity

object FileConverter {

  implicit class UploadedFileToFileEntity(file: UploadedFile) {
    def asFileEntity: FileEntity = {
      FileEntity(id = file.id,
      					 userId = file.userId,
								 hash = file.hash,
								 filename = file.fileName)
    }
  }

  implicit class FileEntityToUploadedFile(fileEntt: FileEntity) {
    def asUploadedFile: UploadedFile = {
      UploadedFile(id = fileEntt.id,
									 userId = fileEntt.userId,
									 hash = fileEntt.hash,
									 fileName = fileEntt.filename)
    }
  }

}
