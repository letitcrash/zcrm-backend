package models
import java.sql.Timestamp

case class TicketAction(id: Option[Int] = None,
                        parentActionId: Option[Int] = None,
                        ticketId: Int,
                        user: User,
                        actionType: Int,
                        comment: Option[String] = None,
                        mail: Option[ExchangeMail] = None,
                        file: Option[UploadedFile] = None,
                        createdAt: Option[Timestamp] = None)
