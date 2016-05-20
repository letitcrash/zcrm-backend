package models

case class Action(id: Option[Int] = None,
                                 parentActionId: Option[Int] = None,
                                 ticketId: Int,
                                 userId: Int,
                                 actionType: Int,
                                 name: String,
                                 comment: Option[String] = None)
