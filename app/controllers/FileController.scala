package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{FileRepository, TaskAttachedMailDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import java.io.File

@Singleton
class FileController @Inject() extends CRMController {
  import utils.JSFormat.uploadedFileFrmt
	

	def uploadFile(userId: Int) = Action.async(parse.multipartFormData){rq =>
		val filePart = rq.body.file("fileUpload").map(file => file)
		val uuid = java.util.UUID.randomUUID.toString
		filePart.get.ref.moveTo(new File(s"/tmp/crm/$uuid"))
		val uploadedFile = UploadedFile(userId = userId,
																		hash = uuid,
																		fileName = filePart.get.filename) 
		FileRepository.saveFile(uploadedFile).map(file => Ok(Json.toJson(file)))
	}
	 
}
