package database_rf

import models.Article
import java.sql.Date
import scala.concurrent.Future

class ArticlesDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class Articles(tag: Tag) extends Table[Article](tag, "tbl_test_articles") {
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
  
  def list(count: Int, offset: Int): Future[Seq[Article]] =
    db.instance.run(articles.drop(offset).take(count).result)
  
  def delete(id: Int): Future[Int] =
    db.instance.run(articles.filter(_.id === id).delete)
}

object ArticlesDBComponent {
  def apply(db: Database): ArticlesDBComponent = new ArticlesDBComponent(db)
}