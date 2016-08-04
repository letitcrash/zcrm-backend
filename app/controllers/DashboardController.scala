package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import utils.ews.EwsAuthUtil
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.DashboardDBRepository
import play.api.libs.json.Json
import scala.concurrent.Future

@Singleton
class DashboardController extends CRMController {
  import utils.JSFormat._

  def getCountsForCompany(companyId: Int) = CRMActionAsync{rq =>
    DashboardDBRepository.getCountsForCompanyById(companyId).map(Json.toJson(_))
  }
 
}
