package models

import java.sql.Date

case class NewsArticle(
    id: Int,
    title: String,
    date: Date,
    author: Int,
    description: Option[String],
    text: String,
    tags: Option[String],
    permission: Int
)