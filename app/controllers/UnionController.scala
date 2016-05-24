package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.UnionDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class UnionController @Inject() extends CRMController {
  import utils.JSFormat.unionFrmt

  //TODO: add permissions check
  def newUnion(companyId: Int) = CRMActionAsync[Union](expectedUnionFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       UnionDBRepository.createUnion(rq.body, companyId).map( union => Json.toJson(union))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def updateUnion(companyId: Int, unionId: Int) = CRMActionAsync[Union](expectedUnionFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      UnionDBRepository.updateUnion(rq.body.copy(id = Some(unionId)), companyId).map( union => Json.toJson(union))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getUnion(companyId: Int, unionId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      UnionDBRepository.getUnionById(unionId).map( union => Json.toJson(union))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllUnions(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      UnionDBRepository.getUnionsByCompanyId(companyId).map( union => Json.toJson(union))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
  
  def deleteUnionById(companyId: Int, unionId:Int) = CRMActionAsync{rq =>
      UnionDBRepository.deleteUnion(unionId).map(deletedUnion => Json.toJson(deletedUnion))
  }

  def searchAllUnionsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      UnionDBRepository.searchUnionByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { UnionDBRepository.getUnionsByCompanyId(companyId).map( unions => Json.toJson(unions)) }
  }
 
}
