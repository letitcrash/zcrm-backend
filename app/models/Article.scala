package models

import java.sql.Date

case class Article(
    id: Int,
    title: String,
    date: Date,
    author: Int,
    body: String,
    tags: Option[String],
    permission: Int
)