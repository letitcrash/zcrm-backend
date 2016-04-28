package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import java.io.File

import database.{FileRepository, TaskAttachedMailDBRepository}
import models._
import controllers.session.{InsufficientRightsException, CRMResponse, CRMResponseHeader}
import security.Security

@Singleton
class FileController @Inject() extends CRMController {
  import utils.JSFormat.uploadedFileFrmt
  import utils.JSFormat.responseFrmt

  // TODO:
  //   - Use CRMActionAsync instead of Action.async, to accomplish that in the right way,
  //     I guess we should implement our CMRAction* helpers leveraging Play's way to do this,
  //     see link for details: https://www.playframework.com/documentation/2.5.x/ScalaActionsComposition
  //
  // KLUDGE:
  //     I decided just to copy paste code from CRMController instead of writing another one
  //     which can handle multipart-data, because for now it would be used only for this endpoint.
  //
  def uploadFile(userId: Int) = Action.async(parse.multipartFormData) { implicit rq =>
    Security.validateHeaders(rq.headers) match {
      case Success(rqHeader) =>
        val filePart = rq.body.file("fileUpload").map(file => file)
        val uuid = java.util.UUID.randomUUID.toString
        filePart.get.ref.moveTo(new File(s"/tmp/crm/$uuid"))
        val uploadedFile = UploadedFile(
          userId = userId,
          hash = uuid,
          fileName = filePart.get.filename)
        FileRepository.saveFile(uploadedFile).map { file =>
          Ok(Json.toJson(CRMResponse(CRMResponseHeader(), Some(Json.toJson(file)))))
        }
      case Failure(ex) =>
        Future {
          Unauthorized(Json.toJson(Map(
            "result" -> "-1234",
            "message" -> "Failed to authenticate",
            "reason" -> ex.getMessage)))
        }

    }
  }

  // FIXME:
  //   - This method works but it also raises an exception:
  //     java.util.NoSuchElementException: http-handler-body-subscriber
  def getFile(userId : Int, id: Int) = CRMActionAsync { req =>
    FileRepository.getFileById(id).map(uploadedFile => new File(s"/tmp/crm/${uploadedFile.hash}"))
  }


  def getFilesListForUser(userId: Int) = CRMActionAsync { req =>
    FileRepository.getFilesForUserByUserId(userId).map(list => Json.toJson(list))
  }
  
  def deleteFile(userId: Int, fileId: Int) = CRMActionAsync { req =>
    // FIXME:
    //   - Should we also delete a file from the filesystem?
    FileRepository.deleteFileById(fileId).map(deleted => Json.toJson(deleted))
  }

}
