package controllers.session

import exceptions.CRMException
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._
import scala.util.{Failure, Success, Try}
import exceptions.{EmptyResultDBException, RecordAlreadyExistDBException}

/**
 * Created by nicros on 2014-05-20.
 */
sealed trait AcceptedReturn {
  def toResp: Result
}
trait AcceptedReturns {
  this: Controller =>

  import utils.JSFormat.responseFrmt

  case class CRMStackTrace(className: String, methodName: String, fileName: String, lineNumber: Int)

  implicit val ttStackTraceWrtr = Json.writes[CRMStackTrace]

  final implicit class OnlyHeader(arg: CRMResponseHeader) extends AcceptedReturn {
    def toResp = Ok(Json.toJson(CRMResponse(arg, None)))
  }

  final implicit class ThrowableAndException(arg: Throwable) extends AcceptedReturn {
    def toResp = InternalServerError(Json.toJson(
      CRMResponse(CRMResponseHeader(-1, getMessage(arg), getStacktrace(arg)), None)))
  }

  final implicit class JSOption(arg: Option[JsValue]) extends AcceptedReturn {
    def toResp = Ok(Json.toJson(CRMResponse(CRMResponseHeader(), arg)))
  }

  final implicit class JS(arg: JsValue) extends AcceptedReturn {
    def toResp = Ok(Json.toJson(CRMResponse(CRMResponseHeader(), Some(arg))))
  }

  final implicit class ErrorCode(code: Int) extends AcceptedReturn {
    def toResp = InternalServerError(Json.toJson(CRMResponse(CRMResponseHeader(response_code = code), None)))
  }

  final implicit class TryReturn(arg: Try[JsValue]) extends AcceptedReturn {
    def toResp = {
      arg match {
        case Success(js) => Ok(Json.toJson(CRMResponse(CRMResponseHeader(), Some(js))))

        case Failure(ex: EmptyResultDBException) =>
          BadRequest(Json.toJson(CRMResponse(CRMResponseHeader(400, Some(ex.getMessage)), None)))

        case Failure(ex: RecordAlreadyExistDBException) =>
          BadRequest(Json.toJson(CRMResponse(CRMResponseHeader(400, Some(ex.getMessage)), None)))

        case Failure(ex: InsufficientRightsException) =>
          Forbidden(Json.toJson(CRMResponse(CRMResponseHeader(401, Some(ex.getMessage)), None)))

        case Failure(ex: CRMException) =>
          Ok(Json.toJson(CRMResponse(CRMResponseHeader(ex.ecode, Some(ex.getMessage)), None)))

        case Failure(ex) =>
          InternalServerError(Json.toJson(CRMResponse(CRMResponseHeader(23, getMessage(ex)), getStacktrace(ex))))
      }
    }
  }

  final implicit class NoneReturn(arg: None.type) extends AcceptedReturn {
    override def toResp = Ok(Json.toJson(CRMResponse(CRMResponseHeader(23, Some("Unknown error")), None)))
  }

  final implicit class StringReturn(arg: String) extends AcceptedReturn {
    override def toResp = Ok(Json.toJson(CRMResponse(CRMResponseHeader(), Some(Json.toJson(Map("message" -> arg))))))
  }

  final implicit class FileReturn(file: java.io.File) extends AcceptedReturn {
    override def toResp = Ok.sendFile(content = file)
  }

  private[AcceptedReturns] def getMessage(ex: Throwable): Option[String] = {
    if (settings.SHOW_ERROR_MESSAGES) Some(ex.getMessage)
    else None
  }



  private[AcceptedReturns] def getStacktrace(ex: Throwable): Option[JsValue] = {
    if (settings.SHOW_STACKTRACES) {
      val errorList = ex.getStackTrace().map { e =>
        CRMStackTrace(e.getClassName, e.getMethodName, e.getFileName, e.getLineNumber)
      }
      Some(Json.toJson(errorList.toList))
    } else None
  }
}

