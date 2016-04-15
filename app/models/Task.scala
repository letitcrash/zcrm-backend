package models

case class Task(id: Option[Int] = None,
                companyId: Int, 
                createdByUser: User,
								assignedToUser: Option[User] = None,
								title: String,
								description: Option[String] = None,
								status: Option[String] = None,
								attachedMails: Option[List[InboxMail]] = None,
								dueDate: Option[String])
