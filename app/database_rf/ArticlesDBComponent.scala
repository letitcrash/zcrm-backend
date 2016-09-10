package database_rf

import models.Article
import java.sql.Date
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArticlesDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class Articles(tag: Tag) extends Table[Article](tag, "crm_articles") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def body = column[String]("body")
    def tags = column[Option[String]]("tags")
    def permission = column[Int]("permission")
    
    def * = (id, title, date, author, body, tags, permission) <>
        ((Article.apply _).tupled, Article.unapply)
  }
  
  val articles = TableQuery[Articles]
  
  def insert(article: Article): Future[Int] =
    db.instance.run(articles += article)
    
  def get(id: Int): Future[Option[Article]] =
    db.instance.run(articles.filter(_.id === id).result.headOption)
  
  def list(query: String, count: Int, offset: Int): Future[Seq[Article]] = {
    db.instance.run(articles.result).map { seq =>
      seq.filter(_.title.contains(query)).drop(offset).take(count)
    }
  }
  
  def delete(id: Int): Future[Int] =
    db.instance.run(articles.filter(_.id === id).delete)
    
  def updateTitle(id: Int, value: String): Future[Int] =
    db.instance.run(articles.filter(_.id === id).map(_.title).update(value))
  
  def updateBody(id: Int, value: String): Future[Int] =
    db.instance.run(articles.filter(_.id === id).map(_.body).update(value))
  
  def updateTags(id: Int, value: Option[String]): Future[Int] =
    db.instance.run(articles.filter(_.id === id).map(_.tags).update(value))
}

object ArticlesDBComponent {
  def apply(db: Database): ArticlesDBComponent = new ArticlesDBComponent(db)
}