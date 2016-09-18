package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Inject

import play.api.mvc.Action
import play.api.libs.json.{Json, JsSuccess, JsError}

import database_rf.{NewsDBComponent, Database}
import models.{NewsArticle, Error, Success}

class NewsController @Inject() (db: Database) extends CRMController {
  val news = NewsDBComponent(db)
  
  def add = CRMActionAsync (parse.json) { req =>
    req.body.validate[NewsArticle] match {
      case json: JsSuccess[NewsArticle] => 
        news.insert(json.get).map { count =>
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
          case ("title", value) => news.updateTitle(id, value)
          case ("desc", value) => news.updateDescription(id, value)
          case ("text", value) => news.updateText(id, value)
          case ("tags", value) => news.updateTags(id, value)
          case ("image", value) => news.updateImage(id, Some(value))
        }

        Future(Ok(Json.toJson(Success(1))))
      }
      case _: JsError => Future(BadRequest(Json.toJson(Error(101, "Wrong data?"))))
    }
  }

  def get(id: Int) = Action.async { _ =>
    news.get(id).map {
      case Some(newsArticle) => Ok(Json.toJson(newsArticle))
      case None => BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }

  def search(query: Option[String], count: Option[Int], offset: Option[Int]) = Action.async { _ =>
    news.list(query.getOrElse(""), count.getOrElse(50), offset.getOrElse(0)).map { seq =>
      if (seq.size > 0) Ok(Json.toJson(seq))
      else BadRequest(Json.toJson(Error(200, "Nothing found.")))
    }
  }
  
  def delete(id: Int) = CRMActionAsync (parse.default) { _ =>
    news.delete(id).map { count =>
      if (count > 0) Ok(Json.toJson(Success(1)))
      else BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }
}