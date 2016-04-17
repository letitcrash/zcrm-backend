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
    for {
      taskEntt <- insertTaskEntity(task.asTaskEntity)
      attachedMailEntts <- task.attachedMails match { case Some(mails) => insertAttachedMailEntities(mails.map(_.asAttachedMailEntt(taskEntt.id.get))).map(list => Some(list))
                                                      case _ =>  Future{ None } }
    } yield (taskEntt, task.createdByUser, task.assignedToUser, attachedMailEntts).asTask
  }

  def updateTask(task: Task): Future[Task] = {
    import utils.converters.TaskConverter._
    import utils.converters.UserConverter._
    for {
      taskWithUser <- updateTaskWithUserByEntity(task.asTaskEntity)
      attachedMails <- getAttachedMailEntitiesByTaskId(taskWithUser._1._1.id.get)
    } yield (taskWithUser._1._1, 
             taskWithUser._1._2.asUser, 
             taskWithUser._2 match {
                                     case Some(userTuple) => Some(userTuple.asUser)
                                     case _ => None },
             attachedMails match {
                                     case mails  => Some(mails)
                                     case _ => None}).asTask
  }

  def getTask(taskId: Int): Future[Task] = {
    import utils.converters.TaskConverter._
    import utils.converters.UserConverter._
    for {
      taskWithUser <- getTaskWitUserById(taskId)
      attachedMails <- getAttachedMailEntitiesByTaskId(taskId)
    } yield (taskWithUser._1._1, 
             taskWithUser._1._2.asUser, 
             taskWithUser._2 match {
                                     case Some(userTuple) => Some(userTuple.asUser)
                                     case _ => None },
             attachedMails match {
                                     case mails  => Some(mails)
                                     case _ => None}).asTask
  }

  def getAllTasks(companyId: Int): Future[List[Task]] = {
    import utils.converters.TaskConverter._
    import utils.converters.UserConverter._
    getTasksWitUserByCompanyId(companyId).flatMap( listTasksWithUser =>
      Future.sequence(
        listTasksWithUser.map( taskWithUser => 
          getAttachedMailEntitiesByTaskId(taskWithUser._1._1.id.get).map( attachedMailEntts => 
              (taskWithUser._1._1, 
               taskWithUser._1._2.asUser, 
               taskWithUser._2 match {
                                       case Some(userTuple) => Some(userTuple.asUser)
                                       case _ => None },
               attachedMailEntts match {
                                       case mails  => Some(mails)
                                       case _ => None}).asTask
            )
         )
       )
     ) 
  }
  

}
