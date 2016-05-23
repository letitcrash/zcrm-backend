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
    //if(rq.header.belongsToCompany(companyId) || rq.header.isAdmin){
       CompanyDBRepository.getCompany(companyId).map( company => Json.toJson(company)) 
    //}else { Future{Failure(new InsufficientRightsException())} }
  }

  def getExpanded(companyId: Int) = CRMActionAsync { rq =>
       CompanyDBRepository.getAggregatedCompany(companyId).map( aggCompany => Json.toJson(aggCompany)) 
  }

  def getAll = CRMActionAsync { rq => 
    CompanyDBRepository.getAllCompanies.map( company => Json.toJson(company)) 
  }

  def searchAllCompaniesByName(pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      CompanyDBRepository.searchCompanyByName(psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { CompanyDBRepository.getAllCompanies.map( company => Json.toJson(company)) }
  }

}
