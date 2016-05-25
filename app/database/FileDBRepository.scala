package database


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{UploadedFile, PagedResult}
import play.api.Logger
import java.io.File
import database.tables.FileEntity
import play.api.mvc.MultipartFormData._
import utils.converters.FileConverter._


object FileDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def saveFile(uploadedFile: UploadedFile): Future[UploadedFile] = {
    insertFileEntity(uploadedFile.asFileEntity).map(savedFile => savedFile.asUploadedFile)
  }

  def getFileById(id: Int): Future[UploadedFile] = {
    getFileEntityById(id).map(_.asUploadedFile) 
  }
  
  def getFilesForUserByUserId(userId: Int): Future[List[UploadedFile]] = {
    getFileEntitiesByUserId(userId).map(list => list.map(_.asUploadedFile))
  } 

  def deleteFileById(id: Int): Future[UploadedFile] = {
    deleteFileEntity(id).map(deleted => deleted.asUploadedFile)
  }

  def searchFileByName(userId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[UploadedFile]] = {
    searchFileEntitiesByName(userId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[UploadedFile](pageSize = dbPage.pageSize,
                          pageNr = dbPage.pageNr,
                          totalCount = dbPage.totalCount,
                          data = dbPage.data.map(_.asUploadedFile))}
  }

}
