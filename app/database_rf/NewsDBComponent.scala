package database_rf

import models.NewsArticle
import java.sql.Date

class NewsDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class News(tag: Tag) extends Table[NewsArticle](tag, "tbl_test_news") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def description = column[Option[String]]("description")
    def text = column[String]("text")
    def tags = column[Option[String]]("tags")
    def permission = column[Int]("permission")

    def * = (id, title, date, author, description, text, tags, permission) <>
        (NewsArticle.tupled, NewsArticle.unapply)
  }
  
  val news = TableQuery[News]
}

object NewsDBComponent {
  def apply(db: Database): NewsDBComponent = new NewsDBComponent(db)
}