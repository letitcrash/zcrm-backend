package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._
import utils.ExpectedFormat._
import controllers.session.InsufficientRightsException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.{TaskDBRepository}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import scala.concurrent.Future

@Singleton
class TaskController @Inject() extends CRMController {
  import utils.JSFormat.taskFrmt

  //TODO: add permissions check
  def newTask(companyId: Int) = CRMActionAsync[Task](expectedTaskFormat) { rq => 
    // if(rq.header.belongsToCompany(companyId)){
       TaskDBRepository.createTask( rq.body.copy(companyId = companyId) ).map( task => Json.toJson(task))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def updateTask(companyId: Int, taskId: Int) = CRMActionAsync[Task](expectedTaskFormat){ rq =>
    // if(rq.header.belongsToCompany(companyId)){
    TaskDBRepository.updateTask(rq.body).map( tasks => Json.toJson(tasks))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getTask(companyId: Int, taskId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    TaskDBRepository.getTask(taskId).map( tasks => Json.toJson(tasks))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }

  //TODO: add permissions check
  def getAllTasks(companyId: Int) = CRMActionAsync { rq =>
    // if(rq.header.belongsToCompany(companyId)){
    TaskDBRepository.getAllTasks(companyId).map( tasks => Json.toJson(tasks))
    // }else{ Future{Failure(new InsufficientRightsException())} }
  }
 
}
