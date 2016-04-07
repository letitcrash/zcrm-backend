package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.mvc._


class Application @Inject() extends Controller {

  def index = Action {
    Ok("Up n running")
  }
}
