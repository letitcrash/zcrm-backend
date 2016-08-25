package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Inject

import play.api.mvc.Action
import play.api.libs.json.{Json, JsSuccess, JsError}

import database_rf.{ArticlesDBComponent, Database}
import models.{Article, Error, Success}

class ArticlesController @Inject() (db: Database) extends CRMController {
  val articles = ArticlesDBComponent(db)

  def add = Action.async (parse.json) { req =>
    req.body.validate[Article] match {
      case json: JsSuccess[Article] => 
        articles.insert(json.get).map { count =>
          if (count > 0) Ok(Json.toJson(Success(1)))
          else BadRequest(Json.toJson(Error(100, "Something went wrong.")))
        }
      case _: JsError => Future(BadRequest(Json.toJson(Error(101, "Wrong data?"))))
    }
  }

  def edit(id: Int) = Action.async { _ =>
    Future(BadRequest(Json.toJson(Error(-1, "Not implemented"))))
  }

  def get(id: Int) = Action.async { _ =>
    articles.get(id).map {
      case Some(article) => Ok(Json.toJson(article))
      case None => BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }

  def search(query: Option[String], count: Option[Int], offset: Option[Int]) = Action.async { _ =>
    articles.list(count.getOrElse(50), offset.getOrElse(0)).flatMap { seq =>
      val result = seq.filter(_.title.contains(query.getOrElse("")))

      if (result.size > 0) Future(Ok(Json.toJson(result)))
      else Future(BadRequest(Json.toJson(Error(200, "Nothing found."))))
    }
  }
  
  def delete(id: Int) = Action.async { _ =>
    articles.delete(id).flatMap { count =>
      if (count > 0) Future(Ok(Json.toJson(Success(1))))
      else Future(BadRequest(Json.toJson(Error(102, "Wrong id?"))))
    }
  }
}