package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.TeamDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class TeamController @Inject() extends CRMController {
  import utils.JSFormat.teamFrmt

  //TODO: add permissions check
  def newTeam(companyId: Int) = CRMActionAsync[Team](expectedTeamFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       TeamDBRepository.createTeam(rq.body, companyId).map( team => Json.toJson(team))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def updateTeam(companyId: Int, teamId: Int) = CRMActionAsync[Team](expectedTeamFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
    	TeamDBRepository.updateTeam(rq.body, companyId).map( team => Json.toJson(team))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getTeam(companyId: Int, teamId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    	TeamDBRepository.getTeamById(teamId).map( team => Json.toJson(team))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllTeams(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
   	 	TeamDBRepository.getTeamsByCompanyId(companyId).map( team => Json.toJson(team))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
	
	def deleteTeamById(companyId: Int, teamId:Int) = CRMActionAsync{rq =>
			TeamDBRepository.deleteTeam(teamId).map(deletedTeam => Json.toJson(deletedTeam))
	}
 
}
