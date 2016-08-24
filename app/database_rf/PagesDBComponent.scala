package database_rf

import models.Page
import java.sql.Date

class PagesDBComponent(val db: Database) {
  import db.config.driver.api._
  
  class Pages(tag: Tag) extends Table[Page](tag, "tbl_test_pages") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def title = column[String]("title")
    def date = column[Date]("date")
    def author = column[Int]("author")
    def description = column[Option[String]]("description")
    def body = column[String]("body")
    def permission = column[Int]("permission")
    
    def * = (id, name, title, date, author, description, body, permission) <>
        (Page.tupled, Page.unapply)
  }
  
  val pages = TableQuery[Pages]
}

object PagesDBComponent {
  def apply(db: Database): PagesDBComponent = new PagesDBComponent(db)
}