package models

import java.sql.Date
import play.api.libs.json.Json

case class Article(
    id: Option[Int],
    title: String,
    date: Date,
    author: Int,
    image: Option[String],
    body: String,
    tags: Option[String],
    permission: Int
)

object Article {
  implicit val newsArticleWrites = Json.format[Article]
}