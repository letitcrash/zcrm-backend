package database_rf

import models.NewsArticle
import java.sql.Date
import scala.concurrent.Future

class NewsDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class News(tag: Tag) extends Table[NewsArticle](tag, "tbl_test_news") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def description = column[Option[String]]("description")
    def text = column[String]("text")
    def tags = column[Option[String]]("tags")
    def permission = column[Int]("permission")

    def * = (id, title, date, author, description, text, tags, permission) <>
        ((NewsArticle.apply _).tupled, NewsArticle.unapply)
  }
  
  val news = TableQuery[News]
    
  def insert(article: NewsArticle): Future[Int] =
    db.instance.run(news += article)
  
  def get(id: Int): Future[Option[NewsArticle]] =
    db.instance.run(news.filter(_.id === id).result.headOption)
    
  def list(count: Int, offset: Int): Future[Seq[NewsArticle]] =
    db.instance.run(news.drop(offset).take(count).result)
  
  def delete(id: Int): Future[Int] =
    db.instance.run(news.filter(_.id === id).delete)
}

object NewsDBComponent {
  def apply(db: Database): NewsDBComponent = new NewsDBComponent(db)
}