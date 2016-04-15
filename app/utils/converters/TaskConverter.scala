package utils.converters

import database.tables.{TaskEntity,UserEntity, TaskAttachedMailEntity}
import models.{Task, InboxMail, User}

object TaskConverter {
  
  implicit class EntityToTask (tuple: (TaskEntity , User, Option[User], Option[List[TaskAttachedMailEntity]])) {

  def asTask: Task = {
      Task(id = tuple._1.id,
           companyId = tuple._1.companyId,
					 createdByUser = tuple._2, 
					 assignedToUser = tuple._3,
					 title = tuple._1.title,
					 description = tuple._1.description,
           status = Some(tuple._1.status), 
           attachedMails = tuple._4 match { case Some(list) => Some(list.map(_.asInboxMail))
                                            case _ => None},
					 dueDate = tuple._1.dueDate)
    }
  }

	implicit class TaskToEntity(task: Task){
			def asTaskEntity: TaskEntity = {
        import play.api.Logger
						val v = TaskEntity(companyId = task.companyId,
	  									 createdByUserId = task.createdByUser.id.get,
                       assignedToUserId = task.assignedToUser match {case Some(user) => task.assignedToUser.get.id
                                                                     case _ => None},
											 title = task.title,
											 description = task.description,
											 dueDate = task.dueDate)
            Logger.info(v.toString)
            v
			}
	}


  implicit class InboxMailToAttachedMailEntt(inboxMail: InboxMail){
    def asAttachedMailEntt(taskId: Int): TaskAttachedMailEntity = {
      TaskAttachedMailEntity(
        taskId = taskId, 
        mailExtId = inboxMail.id.get,
        from = inboxMail.fromEmail.get, 
        subject = inboxMail.subject
        )
    }
  }

  implicit class AttachedMailEnttToInboxMail(attachedMailEntt: TaskAttachedMailEntity) {
    def asInboxMail: InboxMail = {
      InboxMail(
        id = Some(attachedMailEntt.mailExtId),
        fromEmail = Some(attachedMailEntt.from), 
        subject = attachedMailEntt.subject
      )
    }
  }



}
/*


case class TaskAttachedMailEntity(
  id: Option[Int], 
  taskId: Int, 
  mailExtId: String,
  from: String, 
  subject: Option[String] = None
  )


  case class Task(id: Option[Int] = None,
                createdByUser: User,
								assignedToUser: User,
								title: String,
								description: Option[String] = None,
								status: Option[String] = TaskStatus.NEW,
								attachedMails: Option[List[InboxMail]] = None,
								dueDate: Option[String])

case class TaskEntity(
  id: Option[Int], 
  companyId: Int,
  createdByUserId: Int,
  assignedToUserId: Int,
  title: String,
  description: Option[String] = None,
  status: String = TaskStatus.NEW,
  attachedMailId: Option[Int],
  //TODO: should be Date()
  dueDate: Option[String],
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  //TODO: rename UserStatus -> RecrodStatus
  recordStatus: String = UserStatus.ACTIVE
  )
*/

