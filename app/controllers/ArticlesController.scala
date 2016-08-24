package controllers

import database_rf.ArticlesDBComponent
import database_rf.Database
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class ArticlesController @Inject() (db: Database) {
  val dbComponent = ArticlesDBComponent(db)

  /*
    GET			/articles/get					controllers.ArticlesController.get(id: Int)
    GET			/articles/getAll			controllers.ArticlesController.getAll(count: Option[Int], offset: Option[Int])
    GET			/articles/search			controllers.ArticlesController.search(query: String)
    POST		/articles/add					controllers.ArticlesController.add(title: String, author: Int, body: String,
    																	tags: Option[String], permission: Int)
    PUT			/articles/edit				controllers.ArticlesController.edit(id: Int, title: Option[String],
    																	body: Option[String], tags: Option[String], permission: Option[Int])
    DELETE	/articles/delete			controllers.ArticlesController.delete(id: Int)
   */
}