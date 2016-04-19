package models

case class UploadedFile(id: Option[Int] = None,
												userId: Int,
												hash: String,
												fileName: String)
