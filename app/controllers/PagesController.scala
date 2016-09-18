package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.Inject

import play.api.mvc.Action
import play.api.libs.json.{Json, JsSuccess, JsError}

import database_rf.{PagesDBComponent, Database}
import models.{Page, Error, Success}

class PagesController @Inject() (db: Database) extends CRMController {
  val pages = PagesDBComponent(db)

  def add = CRMActionAsync (parse.json) { req =>
    req.body.validate[Page] match {
      case json: JsSuccess[Page] => 
        pages.insert(json.get).map { count =>
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
          case ("alias", value) => pages.updateAlias(id, value)
          case ("title", value) => pages.updateTitle(id, value)
          case ("desc", value) => pages.updateDescription(id, Some(value))
          case ("body", value) => pages.updateBody(id, value)
          case ("image", value) => pages.updateImage(id, Some(value))
        }

        Future(Ok(Json.toJson(Success(1))))
      }
      case _: JsError => Future(BadRequest(Json.toJson(Error(101, "Wrong data?"))))
    }
  }

  def get(id: Int) = Action.async { _ =>
    pages.get(id).map {
      case Some(page) => Ok(Json.toJson(page))
      case None => BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }

  def search(query: Option[String], count: Option[Int], offset: Option[Int]) = Action.async { _ =>
    pages.list(query.getOrElse(""), count.getOrElse(50), offset.getOrElse(0)).map { seq =>
      if (seq.size > 0) Ok(Json.toJson(seq))
      else BadRequest(Json.toJson(Error(200, "Nothing found.")))
    }
  }
  
  def delete(id: Int) = CRMActionAsync (parse.default) { _ =>
    pages.delete(id).map { count =>
      if (count > 0) Ok(Json.toJson(Success(1)))
      else BadRequest(Json.toJson(Error(102, "Wrong id?")))
    }
  }
}