package models

import java.sql.Date
import play.api.libs.json.Json

case class NewsArticle(
    id: Option[Int],
    title: String,
    date: Date,
    author: Int,
    description: Option[String],
    text: String,
    tags: Option[String],
    permission: Int
)

object NewsArticle {
  implicit val newsArticleWrites = Json.format[NewsArticle]
}