package controllers

import database_rf.NewsDBComponent
import database_rf.Database
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class NewsController @Inject() (db: Database) extends CRMController {
  val dbComponent = NewsDBComponent(db)

  /*
    GET			/news/get					controllers.NewsController.get(id: Int)
    GET			/news/getAll			controllers.NewsController.getAll(count: Option[Int], offset: Option[Int])
    GET			/news/search			controllers.NewsController.search(query: String)
    POST		/news/add					controllers.NewsController.add(title: String, author: Int, description: Option[String],
    															text: String, tags: Option[String], permission: Int)
    PUT			/news/edit				controllers.NewsController.edit(id: Int, title: Option[String],
    															description: Option[String], text: Option[String], tags: Option[String],
    															permission: Option[Int])
    DELETE	/news/delete			controllers.NewsController.delete(id: Int)
   */
}