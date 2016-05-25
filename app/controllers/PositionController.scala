package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import java.io.File

import database.PositionDBRepository
import models._
import controllers.session.{InsufficientRightsException, CRMResponse, CRMResponseHeader}
import security.Security

@Singleton
class PositionController @Inject() extends CRMController {
  import utils.ExpectedFormat.expectedPositionFormat
  import utils.JSFormat.positionFrmt 
  
  
  def postPosition(companyId: Int) = CRMActionAsync[Position](expectedPositionFormat){ rq =>
    PositionDBRepository.savePosition(rq.body, companyId)
      .map( position => Json.toJson(position))
  }

  def putPosition(companyId: Int, positionId: Int) = CRMActionAsync[Position](expectedPositionFormat){ rq =>
    PositionDBRepository.changePosition(rq.body, companyId).map( position => Json.toJson(position))
  }

  def deletePosition(companyId: Int, positionId: Int) = CRMActionAsync{rq =>
    PositionDBRepository.removePosition(positionId).map( deletedPosition => Json.toJson(deletedPosition))
  }

  def getPosition(companyId: Int, positionId: Int) = CRMActionAsync { rq => 
    PositionDBRepository.getPositionById(positionId).map( position => Json.toJson(position))
  }

  def getAllPositions(companyId: Int) = CRMActionAsync { rq => 
    PositionDBRepository.getPositionsByCompanyId(companyId).map( positions => Json.toJson(positions))
  }

  def searchAllPositionsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      PositionDBRepository.searchPositionByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { PositionDBRepository.getPositionsByCompanyId(companyId).map( positions => Json.toJson(positions)) }
  }

}
