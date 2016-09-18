package models

import java.sql.Date
import play.api.libs.json.Json

case class Page(
    id: Option[Int],
    alias: String,
    title: String,
    date: Date,
    author: Int,
    description: Option[String],
    image: Option[String],
    body: String,
    permission: Int
)

object Page {
  implicit val newsArticleWrites = Json.format[Page]
}