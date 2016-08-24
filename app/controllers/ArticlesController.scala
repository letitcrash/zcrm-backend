package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.mvc.Action
import database_rf.{ArticlesDBComponent, Database}

class ArticlesController @Inject() (db: Database) extends CRMController {
  val dbComponent = ArticlesDBComponent(db)

  def add = Action.async { _ => Future(Ok("Response\n")) }
  
  def edit(id: Int) = Action.async { _ => Future(Ok("Response\n")) }

  def get(id: Int) = Action.async { _ => Future(Ok("Response\n"))}

  def search(query: Option[String], count: Option[Int], offset: Option[Int]) =
    Action.async { _ => Future(Ok("Response\n")) }
  
  def delete(id: Int) = Action.async { _ => Future(Ok("Response\n")) }
}