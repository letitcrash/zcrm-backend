package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.CompanyDBRepository
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class CompanyController @Inject() extends CRMController {
  import utils.JSFormat.companyFrmt

  def get(companyId: Int) = CRMActionAsync { rq =>
    if(rq.header.isCompanyOwnerOrManagerOrAdmin(companyId)){
       CompanyDBRepository.getCompany(companyId)
         .map( company => Json.toJson(company)) 
    }else { Future{Failure(new InsufficientRightsException())} }
  }

}
