package models

case class Ticket(id: Option[String] = None,
                  companyId: Int,
                  createdByUserId: Int,
                  requestedByUserId: Int,
                  assignedToUserId: Int,
                  assignedToTeamId: Option[Int] = None,
                  commentId: Option[Int] = None,
                  status: Int,
                  subject: String,
                  description: Option[String] = None)
