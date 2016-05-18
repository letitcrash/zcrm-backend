package database

import models.InboxMail

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Task
import play.api.Logger


//TODO: move ALL methods to TaskRepo
object TaskAttachedMailDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._
  
  def saveInboxMailAsAttachedMail(inboxMail: InboxMail, taskId: Int): Future[InboxMail] = {
    import utils.converters.TaskConverter._
    insertAttachedMailEntity(inboxMail.asAttachedMailEntt(taskId)).map(_.asInboxMail)
  }
 
  def removeInboxMailFromTask(attachedMail: InboxMail): Future[InboxMail] = {
      import utils.converters.TaskConverter._
      deleteAttachedMailEntityByExtMailId(attachedMail.id.get).map(_.asInboxMail)
  } 

}
