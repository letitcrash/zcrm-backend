package database_rf

import models.Article
import java.sql.Date

class ArticlesDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class Articles(tag: Tag) extends Table[Article](tag, "tbl_test_articles") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def body = column[String]("body")
    def tags = column[Option[String]]("tags")
    def permission = column[Int]("permission")
    
    def * = (id, title, date, author, body, tags, permission) <>
        (Article.tupled, Article.unapply)
  }
  
  val articles = TableQuery[Articles]
}

object ArticlesDBComponent {
  def apply(db: Database): ArticlesDBComponent = new ArticlesDBComponent(db)
}