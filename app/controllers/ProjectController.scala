package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.ProjectDBRepository 
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class ProjectController @Inject() extends CRMController {
  import utils.JSFormat.projectFrmt

  //TODO: add permissions check
  def newProject(companyId: Int) = CRMActionAsync[Project](expectedProjectFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       ProjectDBRepository.createProject(rq.body, companyId).map( project => Json.toJson(project))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def updateProject(companyId: Int, projectId: Int) = CRMActionAsync[Project](expectedProjectFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
      ProjectDBRepository.updateProject(rq.body.copy(id = Some(projectId)), companyId).map( project => Json.toJson(project))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }


  //TODO: add permissions check
  def getProject(companyId: Int, projectId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      ProjectDBRepository.getProjectById(projectId).map( project => Json.toJson(project))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllProjects(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
      ProjectDBRepository.getProjectsByCompanyId(companyId).map( project => Json.toJson(project))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
  
  def deleteProjectById(companyId: Int, projectId:Int) = CRMActionAsync{rq =>
      ProjectDBRepository.deleteProject(projectId).map(deletedProject => Json.toJson(deletedProject))
  }

  def searchAllProjectsByName(companyId: Int, pageSize: Option[Int], pageNr: Option[Int], searchTerm: Option[String]) = CRMActionAsync{rq =>
    import utils.JSFormat._
    if (pageNr.nonEmpty || pageSize.nonEmpty || searchTerm.nonEmpty) {
      val psize = pageSize.getOrElse(10)
      val pnr = pageNr.getOrElse(1)
      ProjectDBRepository.searchProjectByName(companyId, psize, pnr, searchTerm).map(page => Json.toJson(page))
    } else { ProjectDBRepository.getProjectsByCompanyId(companyId).map( projects => Json.toJson(projects)) }
  }
 
}
