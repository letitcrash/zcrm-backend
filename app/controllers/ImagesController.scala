package controllers

import java.io.File

class ImagesController extends CRMController {

  def upload = CRMAction (parse.multipartFormData) { req =>
    val path = req.body.file("image").map { file =>
      val ext = file.contentType.getOrElse("image/jpg").split("/").last
      val fileRef = file.ref

      fileRef.moveTo(new File(s"images/${fileRef.hashCode}.${ext}"), false).getPath
    }

    Ok(path.get)
  }
}
