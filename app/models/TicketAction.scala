package models
import java.sql.Timestamp

case class TicketAction(id: Option[Int] = None,
                        parentActionId: Option[Int] = None,
                        ticketId: Int,
                        user: User,
                        actionType: Int,
                        comment: Option[String] = None,
                        createdAt: Option[Timestamp] = None)
