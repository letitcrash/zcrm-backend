package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future
import java.io.File

import database.PositionRepository
import models._
import controllers.session.{InsufficientRightsException, CRMResponse, CRMResponseHeader}
import security.Security

@Singleton
class PositionController @Inject() extends CRMController {
  import utils.ExpectedFormat.expectedPositionFormat
  import utils.JSFormat.positionFrmt 
  
  
  def postPosition(companyId: Int) = CRMActionAsync[Position](expectedPositionFormat){ rq =>
    PositionRepository.savePosition(rq.body, companyId)
      .map( position => Json.toJson(position))
  }

  def putPosition(companyId: Int, positionId: Int) = CRMActionAsync[Position](expectedPositionFormat){ rq =>
    PositionRepository.changePosition(rq.body, companyId).map( position => Json.toJson(position))
  }

  def deletePosition(companyId: Int, positionId: Int) = CRMActionAsync{rq =>
    PositionRepository.removePosition(positionId).map( deletedPosition => Json.toJson(deletedPosition))
  }

  def getPosition(companyId: Int, positionId: Int) = CRMActionAsync { rq => 
    PositionRepository.getPositionById(positionId).map( position => Json.toJson(position))
  }

  def getAllPositions(companyId: Int) = CRMActionAsync { rq => 
    PositionRepository.getPositionsByCompanyId(companyId).map( positions => Json.toJson(positions))
  }

}
