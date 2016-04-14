package utils.converters

import database.tables.{TaskEntity,UserEntity, TaskAttachedMailEntity}
import models.Task

object TaskConverter {
  
  implicit class EntityToTask (tuple: (TaskEntity , UserEntity, UserEntity, List[TaskAttachedMailEntity])) {

    def asTask: Task = {
      models.Task(id: Option[Int] = None,
									createdByUser = tuple._2.id.get,
									assignedToUser = tuple._3.id.get,
									title = taskEntt.title,
									description = Some(taskEntt.description),
									attachedMails: List[InboxMail],
									dueDate = taskEntt.dueDate)
    }
  }

	implicit class TaskToEntity(task: Task){
			def asTaskEntity: TaskEntity = {
						TaskEntity(companyId = company.id.get
	  									 createdByUserId = task,createdByUser.id.get,
											 assignedToUserId = task.assignedToUser.id.get,
											 title = task.title,
											 description = Some(task.description) ,
											 attachedMailId = task.attachedMails[0].id ,
											 dueDate: Option[String],
											
)
			}
	
	}

case class Task(id: Option[Int] = None,
                company: Company,
                createdByUser: User,
								assignedToUser: User,
								title: String,
								description: Option[String] = None,
								status: String = TaskStatus.NEW,
								attachedMails: List[InboxMail],
								dueDate: Option[String],
								recordStatus: String = UserStatus.ACTIVE)
}
