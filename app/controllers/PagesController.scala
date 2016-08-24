package controllers

import database_rf.PagesDBComponent
import database_rf.Database
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class PagesController @Inject() (db: Database) {
  val dbComponent = PagesDBComponent(db)
  
  /*
    GET			/pages/get				controllers.PagesController.get(id: Int)
    GET			/pages/getAll			controllers.PagesController.getAll(count: Option[Int], offset: Option[Int])
    GET			/pages/search			controllers.PagesController.search(query: String)
    POST		/pages/add				controllers.PagesController.add(title: String, author: Int, description: Option[String],
    															body: String, permission: Int)
    PUT			/pages/edit				controllers.PagesController.edit(id: Int, title: Option[String],
    															description: Option[String], body: Option[String], permission: Option[Int])
    DELETE	/pages/delete			controllers.PagesController.delete(id: Int)
   */
}