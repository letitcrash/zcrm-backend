package models

import play.api.libs.json.Json

case class Success(code: Int)

object Success {
  implicit val errorWrites = Json.format[Success]
}