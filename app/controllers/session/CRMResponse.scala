package controllers.session

import play.api.libs.json.JsValue

/**
 * Created by nicros on 07/05/14.
 */
case class CRMResponseHeader(
  response_code: Int = 0,
  error_message: Option[String] = None,
  detailed_error: Option[JsValue] = None)


case class CRMResponse(
  header: CRMResponseHeader,
  body: Option[JsValue])
