package controllers.session

import play.api.libs.json.{JsError, JsValue}

/**
 * Created by nicros on 09/05/14.
 */
object ResponseHelper {

  def makeHeader(): CRMResponseHeader = {
    new CRMResponseHeader(0, None)
  }

  def makeHeader(error: String): CRMResponseHeader = {
    new CRMResponseHeader(1, Some(error))
  }

  def makeHeader(errorMessage: String, details: JsError): CRMResponseHeader = {
    new CRMResponseHeader(1, Some(errorMessage), Some(JsError.toJson(details)))
  }

  def makeHeader(responseCode: Int, error: String): CRMResponseHeader = {
    new CRMResponseHeader(responseCode, Some(error))
  }


  def make(header: CRMResponseHeader): CRMResponse = {
    new CRMResponse(header, None)
  }

  def make(body: JsValue): CRMResponse = {
    new CRMResponse(CRMResponseHeader(), Some(body))
  }

  def make(header: CRMResponseHeader, body: JsValue): CRMResponse = {
    new CRMResponse(header, Some(body))
  }

  def make(errorMessage: String, error: JsError): CRMResponse = {
    new CRMResponse(makeHeader(errorMessage, error), None)
  }
}
