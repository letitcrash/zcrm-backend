package database

import models.{PagedResult, Project, User, Team, Client}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.converters.ProjectConverter._
import utils.converters.ProjectMemberConverter._
import utils.converters.ClientConverter._

import scala.concurrent.Future


object ProjectDBRepository {
  import database.gen.current.dao._

  def createProject(project: Project, companyId: Int): Future[Project] = {
    insertProject(project.asProjectEntity)
          .map(inserted => inserted.asProject())
  }

  def updateProject(project: Project, companyId: Int): Future[Project] = {
    updateProjectEntity(project.asProjectEntity)
          .map(updated => updated.asProject())
  }

  def deleteProject(projectId: Int): Future[Project] = {
    softDeleteProjectById(projectId)
          .map(deleted => deleted.asProject())
  }

  def getProjectById(id: Int): Future[Project] = {
    for{
       project        <- getProjectEntityById(id)
       clients        <- getClientEntitiesByProjectId(id) 
       countNew       <- getCountNewTicket(id)
       countOpen      <- getCountOpenTicket(id)
       countPostponed <- getCountPostponedTicket(id)
       countResolved  <- getCountResolvedTicket(id)
       clients        <- getClientEntitiesByProjectId(id)
    }yield(project.asProject(None, Some(countNew), Some(countOpen), Some(countPostponed), Some(countResolved), Some(clients.map(_.asClient))))
  }

  def getProjectWithMembersByProjectId(id: Int): Future[Project] = {
    for{
        project <- getProjectEntityById(id)
        members <- getProjectMembersWithUsersByProjectId(id).map(_.asProjectMemberWithUsers)
    }yield(project.asProject(members.members))
  }

  def getProjectsByCompanyId(companyId: Int): Future[List[Project]] = {
    getProjectEntitiesByCompanyId(companyId).flatMap(list =>
      Future.sequence( 
          list.map(project =>
              for{
                 countNew       <- getCountNewTicket(project.id.get)
                 countOpen      <- getCountOpenTicket(project.id.get)
                 countPostponed <- getCountPostponedTicket(project.id.get)
                 countResolved  <- getCountResolvedTicket(project.id.get)
                 clients        <- getClientEntitiesByProjectId(project.id.get)
              }yield(project.asProject(None, Some(countNew), Some(countOpen), Some(countPostponed), Some(countResolved),  Some(clients.map(_.asClient)))))))
  } 


  def searchProjectByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Project]] = {
    searchProjectEntitiesByName(companyId, pageSize, pageNr, searchTerm).flatMap{dbPage =>
        Future.sequence(
            dbPage.data.map(project =>
              for{
                 countNew       <- getCountNewTicket(project.id.get)
                 countOpen      <- getCountOpenTicket(project.id.get)
                 countPostponed <- getCountPostponedTicket(project.id.get)
                 countResolved  <- getCountResolvedTicket(project.id.get)
                 clients        <- getClientEntitiesByProjectId(project.id.get)
              }yield(project.asProject(None, Some(countNew), Some(countOpen), Some(countPostponed), Some(countResolved),  Some(clients.map(_.asClient))))))
                  .map(projectList =>
                       PagedResult[Project](pageSize = dbPage.pageSize,
                                            pageNr = dbPage.pageNr,
                                            totalCount = dbPage.totalCount,
                                            data = projectList))
    }
  }

  def addMembers(projectId: Int, users: List[User]): Future[List[User]] = {
    deleteAllMembersByProjectId(projectId).flatMap(count =>
      insertProjectMembers(users.map( u => (projectId, u.id.get).asProjectMemberEntt))
        .map(pair => users))
  }

  def addTeams(projectId: Int, teams: List[Team]): Future[List[Team]] = {
    deleteAllTeamsByProjectId(projectId).flatMap(count => 
      insertProjectTeamMembers(teams.map( t => (projectId, t.id.get).asProjectTeamMemberEntt))
        .map( pair => teams))
  }

  def addClients(projectId: Int, clients: List[Client]): Future[List[Client]] = {
    deleteAllClientsByProjectId(projectId).flatMap(count =>
      insertProjectClientEntities(clients.map(_.asClientEntity), projectId)
        .map(_ => clients))
  }
}
