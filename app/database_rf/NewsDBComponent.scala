package database_rf

import models.NewsArticle
import java.sql.Date
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class NewsDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class News(tag: Tag) extends Table[NewsArticle](tag, "crm_news") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def description = column[Option[String]]("description")
    def image = column[Option[String]]("image")
    def text = column[String]("text")
    def tags = column[Option[String]]("tags")
    def permission = column[Int]("permission")

    def * = (id, title, date, author, description, image, text, tags, permission) <>
        ((NewsArticle.apply _).tupled, NewsArticle.unapply)
  }
  
  val news = TableQuery[News]
    
  def insert(article: NewsArticle): Future[Int] =
    db.instance.run(news += article)
  
  def get(id: Int): Future[Option[NewsArticle]] =
    db.instance.run(news.filter(_.id === id).result.headOption)
    
  def list(query: String, count: Int, offset: Int): Future[Seq[NewsArticle]] = {
    db.instance.run(news.result).map { seq =>
      seq.filter(_.title.contains(query)).drop(offset).take(count)
    }
  }
  
  def delete(id: Int): Future[Int] =
    db.instance.run(news.filter(_.id === id).delete)
    
  def updateTitle(id: Int, value: String): Future[Int] =
    db.instance.run(news.filter(_.id === id).map(_.title).update(value))
    
  def updateDescription(id: Int, value: String): Future[Int] =
    db.instance.run(news.filter(_.id === id).map(_.description).update(Some(value)))
    
  def updateText(id: Int, value: String): Future[Int] =
    db.instance.run(news.filter(_.id === id).map(_.text).update(value))
    
  def updateTags(id: Int, value: String): Future[Int] =
    db.instance.run(news.filter(_.id === id).map(_.tags).update(Some(value)))
  
  def updateImage(id: Int, value: Option[String]): Future[Int] =
    db.instance.run(news.filter(_.id === id).map(_.image).update(value))
}

object NewsDBComponent {
  def apply(db: Database): NewsDBComponent = new NewsDBComponent(db)
}