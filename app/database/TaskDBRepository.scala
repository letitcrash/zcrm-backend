package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Task
import play.api.Logger


object TaskDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createTask(task: Task): Future[Task] = {
    import utils.converters.TaskConverter._
    //TODO: should be transactionally
    import play.api.libs.json.Json
    import utils.JSFormat._
    Logger.info(Json.toJson(task).toString)

    for {
      taskEntt <- insertTaskEntity(task.asTaskEntity)
      attachedMailEntts <- task.attachedMails match { case Some(mails) => insertAttachedMailEntities(mails.map(_.asAttachedMailEntt(taskEntt.id.get))).map(list => Some(list))
                                                      case _ =>  Future{ None } }
    } yield (taskEntt, task.createdByUser, task.assignedToUser, attachedMailEntts).asTask


  }

}
