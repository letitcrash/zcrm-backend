package models

case class TicketAction(id: Option[Int] = None,
                        parentActionId: Option[Int] = None,
                        ticketId: Int,
                        userId: Int,
                        actionType: Int,
                        comment: Option[String] = None)
