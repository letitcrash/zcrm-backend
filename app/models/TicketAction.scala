package models

case class TicketAction(id: Option[Int] = None,
                                 parentActionId: Option[Int] = None,
                                 ticketId: Int,
                                 userId: Int,
                                 actionType: Int,
                                 name: String,
                                 comment: Option[String] = None)
