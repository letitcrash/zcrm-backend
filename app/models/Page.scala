package models

import java.sql.Date

case class Page(
    id: Int,
    name: String,
    title: String,
    date: Date,
    author: Int,
    description: Option[String],
    body: String,
    permission: Int
)