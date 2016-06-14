package database

import models.{PagedResult, Project}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.converters.ProjectConverter._

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
    getProjectEntityById(id).map(project => project.asProject())
  }

  def getProjectWithMembersByProjectId(id: Int): Future[Project] = {
    import utils.converters.ProjectMemberConverter._
    for{
        project <- getProjectEntityById(id)
        members <- getProjectMembersWithUsersByProjectId(id).map(_.asProjectMemberWithUsers)
    }yield(project.asProject(members.members))
  }

  def getProjectsByCompanyId(companyId: Int): Future[List[Project]] = {
    getProjectEntitiesByCompanyId(companyId).map(list => list.map(_.asProject()))
  } 


  def searchProjectByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Project]] = {
    searchProjectEntitiesByName(companyId, pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Project](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asProject()))}
  }
}
