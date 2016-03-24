package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.{Json, JsValue}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends CRMController {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val expectedValidationFormat = Json.toJson(Map(
    "foo"    -> Json.toJson("[M](string) foo string"),
    "bar"   -> Json.toJson("[M](string) bar string")
  ))

  implicit val validationFrmt = Json.format[Validation]

  def validationTest = CRMAction[Validation](expectedValidationFormat) { rq => 

    Json.toJson(Map("response"  -> Json.toJson("This is response JSON.")))

  }

}

case class Validation(
  foo: String, 
  bar: String 
)


