package database_rf

import models.Page
import java.sql.Date
import scala.concurrent.Future

class PagesDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class Pages(tag: Tag) extends Table[Page](tag, "crm_pages") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def alias = column[String]("alias")
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def description = column[Option[String]]("description")
    def body = column[String]("body")
    def permission = column[Int]("permission")
    
    def * = (id, alias, title, date, author, description, body, permission) <>
        ((Page.apply _).tupled, Page.unapply)
  }
  
  val pages = TableQuery[Pages]

  def insert(page: Page): Future[Int] =
    db.instance.run(pages += page)
    
  def get(id: Int): Future[Option[Page]] =
    db.instance.run(pages.filter(_.id === id).result.headOption)
  
  def list(count: Int, offset: Int): Future[Seq[Page]] =
    db.instance.run(pages.drop(offset).take(count).result)
    
  def delete(id: Int): Future[Int] =
    db.instance.run(pages.filter(_.id === id).delete)
    
  def updateAlias(id: Int, value: String): Future[Int] =
    db.instance.run(pages.filter(_.id === id).map(_.alias).update(value))
  
  def updateTitle(id: Int, value: String): Future[Int] =
    db.instance.run(pages.filter(_.id === id).map(_.title).update(value))
    
  def updateDescription(id: Int, value: Option[String]): Future[Int] =
    db.instance.run(pages.filter(_.id === id).map(_.description).update(value))
    
  def updateBody(id: Int, value: String): Future[Int] =
    db.instance.run(pages.filter(_.id === id).map(_.body).update(value))
}

object PagesDBComponent {
  def apply(db: Database): PagesDBComponent = new PagesDBComponent(db)
}