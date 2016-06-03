package database

import models.{Employee, TeamWithMember}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Team, TeamGroup, PagedResult}
import play.api.Logger
import utils.converters.TeamConverter._


object TeamDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createTeam(team: Team, companyId: Int): Future[Team] = {
    insertTeam(team.asTeamEntity(companyId))
          .map(inserted => inserted.asTeam)
  }

  def updateTeam(team: Team, companyId: Int): Future[Team] = {
    updateTeamEntity(team.asTeamEntity(companyId))
          .map(updated => updated.asTeam)
  }

  def deleteTeam(teamId: Int): Future[Team] = {
    softDeleteTeamById(teamId)
          .map(deleted => deleted.asTeam)
  }

  def getTeamById(id: Int): Future[Team] = {
    getTeamEntityById(id).map(team => team.asTeam)
  }

  def getTeamsByCompanyId(companyId: Int): Future[List[Team]] = {
    getTeamEntitiesByCompanyId(companyId).map(list => list.map(_.asTeam))
  } 


  def addUserToTeamGroup(teamGroup: TeamGroup): Future[TeamGroup] = {
    insertTeamGroup(teamGroup.asEntity).map( tg => tg.asTeamGroup)
  }

  def searchTeamByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Team]] = {
    searchTeamEntitiesByName(companyId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Team](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asTeam))}
  }

  def createTeamWithMembers(teamWithMembers: TeamWithMember, companyId: Int): Future[TeamWithMember] ={
    insertTeam(teamWithMembers.asTeam.asTeamEntity(companyId)).flatMap(newTeam => 
       teamWithMembers.members.map( mbs => 
         insertTeamGroups( mbs.map( m =>
           m.asTeamGroupEntt(newTeam.id.get))).flatMap( list =>
             Future(teamWithMembers))
       ).getOrElse(Future(teamWithMembers)))
  }
  

}
