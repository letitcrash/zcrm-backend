package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import controllers.session._
import play.api.libs.json.{JsError, Reads, JsValue, Json, JsSuccess}
import scala.util.{Success, Failure, Try}
import security.Security

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future


@Singleton
class CRMController @Inject() extends Controller with AcceptedReturns  {
  import utils.JSFormat.{responseFrmt}
  
  def jsonError(message: String, error: JsError) = Json.toJson(
    CRMResponse(
      CRMResponseHeader(
        response_code = 22,
        error_message = Some(message),
        detailed_error = Some(JsError.toJson(error))
      ),
     None))

  object CRMAction {
    def apply[T](expectedFormat: JsValue)(bodyFn: CRMRequest[T] => AcceptedReturn)
                (implicit reads: Reads[T]) =
      //TODO: check expectedFormat if works
      Action(parse.anyContent) {
        implicit req =>
          validateHeaders(req.headers) { ttHeader =>
            req.body.asJson.fold(BadRequest(expectedFormat)) { jsBody =>
              jsBody.validate[T](reads).map { body =>
                bodyFn(CRMSimpleRequest(ttHeader, body)).toResp
              } recoverTotal (e => BadRequest(jsonError("Invalid format", e)))
            }
          }
      }

   def apply(bodyFn: CRMRequest[None.type] => AcceptedReturn) =
     Action(parse.anyContent) {
        implicit req =>
          validateHeaders(req.headers) { ttHeader =>
            bodyFn(CRMSimpleRequest(ttHeader, None)).toResp
         }
    }
  }

  object CRMActionAsync {
    def apply[T](expectedFormat: JsValue)(bodyFn: CRMRequest[T] => Future[AcceptedReturn])
                (implicit reads: Reads[T]) =
      Action.async (parse.anyContent) {
        implicit req =>
          Security.validateHeaders(req.headers) match {
           case Success(rqHeader) =>
             req.body.asJson match {
               case jsBody: Some[JsValue] =>
                              jsBody.get.validate[T](reads)  match {
                                 case s: JsSuccess[T] => 
                                   bodyFn(CRMSimpleRequest(rqHeader, s.get)).map(_.toResp)
                                    .recover{case e: Exception => BadRequest( Json.toJson(Map("result" -> "-1233",
                                                                                "message" -> "Exception occured",
                                                                                "reason" -> e.getMessage)))}
                                 case e: JsError => Future{ BadRequest(expectedFormat) }
                               }
                case None => Future { BadRequest(jsonError("Invalid format", JsError("JSON required")))}
            }
            case Failure(ex) =>
              Future { Unauthorized(Json.toJson(Map("result" -> "-1234",
                                     "message" -> "Failed to authenticate",
                                     "reason" -> ex.getMessage))) }
          }
      }

   def apply(bodyFn: CRMRequest[None.type] => Future[AcceptedReturn]) =
     Action.async (parse.anyContent) {
        implicit req => 
          Security.validateHeaders(req.headers) match {
            case Success(rqHeader) => 
              bodyFn(CRMSimpleRequest(rqHeader, None)).map(_.toResp)
                  .recover { case e: Exception => BadRequest( Json.toJson(Map("result" -> "-1233",
                                                                "message" -> "Exception occured",
                                                                "reason" -> e.getMessage)))}
            case Failure(ex) =>
              Future { Unauthorized(Json.toJson(Map("result" -> "-1234",
                                     "message" -> "Failed to authenticate",
                                     "reason" -> ex.getMessage))) }
          }
    }
  }

  def validateHeaders(headers: Headers)
                     (fn: CRMRequestHeader => Result): Result = {

    Security.validateHeaders(headers) match {

      case Success(rqHeader) =>
        val startTime = System.currentTimeMillis()

        val response = fn(rqHeader) match {

          case resp if resp.header.status == 200 =>
            // When request is successfully handled, refresh headers.
            Security.updateHeaderToken(rqHeader).map { newHeader =>
              resp.withHeaders((settings.API_KEY_HEADER, newHeader))
            } getOrElse InternalServerError("Failed to encrypt auth token")

          case resp => resp
        }
        if(settings.LOG_RESPONSE_TIMES) {
          Logger.info(f"Handled request in ${System.currentTimeMillis() - startTime}ms")
        }

        response

      case Failure(ex) =>
        Unauthorized(Json.toJson(Map("result" -> "-1234",
                                     "message" -> "Failed to authenticate",
                                     "reason" -> ex.getMessage)))
    }
  }



}
