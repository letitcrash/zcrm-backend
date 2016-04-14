package models

case class Task(id: Option[Int] = None,
                createdByUser: User,
								assignedToUser: User,
								title: String,
								description: Option[String] = None,
								status: String = TaskStatus.NEW,
								attachedMails: List[InboxMail],
								dueDate: Option[String],
								recordStatus: String = UserStatus.ACTIVE)
