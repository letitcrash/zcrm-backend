package utils


import models._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import java.sql.Timestamp
import controllers.session.CRMRequestHeader
import controllers.session.CRMResponseHeader
import play.api.libs.json.JsSuccess
import models.ContactProfile
import controllers.session.CRMResponse


/**
 * Created by nicros on 2014-05-12.
 */
object JSFormat {
  import play.api.libs.json.Json

  // Custom formats
  // ~~~~~
  implicit val timestmapFrmt      = new TimestampFormat


  // Request formats
  // ~~~~~
  implicit val responseHeaderFrmt = Json.format[CRMResponseHeader]
  implicit val responseFrmt       = Json.format[CRMResponse]

  // Class formats
  // ~~~~~
  implicit val contactProfileFrmt           = Json.format[ContactProfile]
  implicit val userFrmt                     = Json.format[User]
  implicit val employeeFrmt                 = Json.format[Employee]
  implicit val companyFrmt                  = Json.format[Company]
  implicit val teamFrmt                     = Json.format[Team]
  implicit val unionFrmt                    = Json.format[Union]
  implicit val shiftFrmt                    = Json.format[Shift]
  implicit val departmentFrmt               = Json.format[Department]
  implicit val employeeWithLevelFrmt        = Json.format[EmployeeWithLevel]

  implicit val inboxMailFrmt                = Json.format[InboxMail]
  implicit val outboxMailFrmt               = Json.format[OutboxMail]

  implicit val taskFrmt                     = Json.format[Task]
  implicit val mailboxFrmt                  = Json.format[Mailbox]
  implicit val uploadedFileFrmt             = Json.format[UploadedFile]
  implicit val positionFrmt                 = Json.format[Position]
}


private[utils] class TimestampFormat extends Format[java.sql.Timestamp] {
  override def reads(json: JsValue): JsResult[Timestamp] = {
    json.validate[Long] map {
      num =>
        JsSuccess(new Timestamp(num))
    } recoverTotal(e => JsError("could not parse timestamp"))
  }

  override def writes(o: Timestamp): JsValue = {
    Json.toJson(o.getTime)
  }
}
