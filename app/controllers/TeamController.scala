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
  import utils.JSFormat.teamGroupFrmt
  import utils.JSFormat.teamWithMembersFrmt


  def newTeamWithMembers(companyId: Int) = CRMActionAsync[TeamWithMember](expectedTeamFormat) { rq => 
    TeamDBRepository.createTeamWithMembers(rq.body, companyId).map( team => Json.toJson(team))
  }

  //TODO: add permissions check
  def newTeam(companyId: Int) = CRMActionAsync[Team](expectedTeamFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       TeamDBRepository.createTeam(rq.body, companyId).map( team => Json.toJson(team))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }



  //TODO: add permissions check
  def updateTeam(companyId: Int, teamId: Int) = CRMActionAsync[Team](expectedTeamFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      TeamDBRepository.updateTeam(rq.body.copy(id = Some(teamId)), companyId).map( team => Json.toJson(team))
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


  def addUserToTeam(companyId: Int, teamId: Int, userId: Int) = CRMActionAsync{ rq => 
    TeamDBRepository.addUserToTeamGroup(TeamGroup(teamId, userId)).map( tg => Json.toJson(tg))
  }

  def removeUserFromTeam(companyId: Int, teamId: Int, userId: Int) = CRMActionAsync{rq =>
    TeamDBRepository.deleteUserFromTeam(teamId, userId).map(tg => Json.toJson(tg))
  }

  def searchAllTeamsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      TeamDBRepository.searchTeamByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { TeamDBRepository.getTeamsByCompanyId(companyId).map( teams => Json.toJson(teams)) }
  }
 
}
