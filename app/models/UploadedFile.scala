package models

import java.sql.Timestamp

case class UploadedFile(
  id: Option[Int] = None,
  userId: Int,
  hash: String,
  fileName: String,
  createdAt: Option[Timestamp] = None)
