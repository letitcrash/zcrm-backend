package models

case class Ticket(id: Option[Int] = None,
                  companyId: Int,
                  createdByUserId: Int,
                  requestedByUserId: Int,
                  assignedToUserId: Int,
                  assignedToTeamId: Option[Int] = None,
                  commentId: Option[Int] = None,
                  ticketId: Option[String] = None,
                  status: Int,
                  subject: String,
                  description: Option[String] = None)
