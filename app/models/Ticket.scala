package models

case class Ticket(id: Option[String] = None,
                  companyId: Int,
                  createdByUserId: Int,
                  requestedByUserId: Option[Int] = None,
                  requestedByEmail: Option[String] = None,
                  assignedToUserId: Option[Int] = None,
                  assignedToTeamId: Option[Int] = None,
                  status: Int,
                  priority: Int,
                  subject: String,
                  description: Option[String] = None)
