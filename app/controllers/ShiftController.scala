package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.ShiftDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class ShiftController @Inject() extends CRMController {
  import utils.JSFormat.shiftFrmt

  //TODO: add permissions check
  def newShift(companyId: Int) = CRMActionAsync[Shift](expectedShiftFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       ShiftDBRepository.createShift(rq.body, companyId).map( shift => Json.toJson(shift))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def updateShift(companyId: Int, shiftId: Int) = CRMActionAsync[Shift](expectedShiftFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
    	ShiftDBRepository.updateShift(rq.body.copy(id = Some(shiftId)), companyId).map( shift => Json.toJson(shift))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getShift(companyId: Int, shiftId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    	ShiftDBRepository.getShiftById(shiftId).map( shift => Json.toJson(shift))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllShifts(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
   	 	ShiftDBRepository.getShiftsByCompanyId(companyId).map( shift => Json.toJson(shift))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
	
	def deleteShiftById(companyId: Int, shiftId:Int) = CRMActionAsync{rq =>
			ShiftDBRepository.deleteShift(shiftId).map(deletedShift => Json.toJson(deletedShift))
	}
 
}
