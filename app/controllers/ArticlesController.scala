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

  def add = CRMActionAsync (parse.json) { req =>
    req.body.validate[Article] match {
      case json: JsSuccess[Article] => 
        articles.insert(json.get).map { count =>
          if (count > 0) Ok(Json.toJson(Success(1)))
          else BadRequest(Json.toJson(Error(100, "Something went wrong.")))
        }
      case _: JsError => Future(BadRequest(Json.toJson(Error(101, "Wrong data?"))))
    }
  }

  def edit(id: Int) = CRMActionAsync (parse.json) { req =>
    req.body.validate[Map[String, String]] match {
      case json: JsSuccess[Map[String, String]] => {
        json.get.foreach {
          case ("title", value) => articles.updateTitle(id, value)
          case ("body", value) => articles.updateBody(id, value)
          case ("tags", value) => articles.updateTags(id, Some(value))
          case ("image", value) => articles.updateImage(id, Some(value))
        }

        Future(Ok(Json.toJson(Success(1))))
      }
      case _: JsError => Future(BadRequest(Json.toJson(Error(101, "Wrong data?"))))
    }
  }

  def get(id: Int) = Action.async { _ =>
    articles.get(id).map {
      case Some(article) => Ok(Json.toJson(article))
      case None => BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }

  def search(query: Option[String], count: Option[Int], offset: Option[Int]) = Action.async { _ =>
    articles.list(query.getOrElse(""), count.getOrElse(50), offset.getOrElse(0)).map { seq =>
      if (seq.size > 0) Ok(Json.toJson(seq))
      else BadRequest(Json.toJson(Error(200, "Nothing found.")))
    }
  }
  
  def delete(id: Int) = CRMActionAsync (parse.default) { _ =>
    articles.delete(id).map { count =>
      if (count > 0) Ok(Json.toJson(Success(1)))
      else BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }
}