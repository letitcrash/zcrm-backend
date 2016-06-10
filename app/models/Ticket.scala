package models

case class Ticket(id: Option[String] = None,
                  companyId: Int,
                  createdByUserId: Int,
                  requestedByUserId: Option[Int] = None,
                  members: Option[List[User]] = None, 
                  requestedByEmail: Option[String] = None,
                  assignedToUserId: Option[Int] = None,
                  assignedToTeamId: Option[Int] = None,
                  status: Int,
                  priority: Int,
                  subject: String,
                  description: Option[String] = None)

case class AggregatedTicket(id: Option[String] = None,
                            company: Company,
                            createdByUser: User,
                            requestedByUser: Option[User] = None,
                            requestedByEmail: Option[String] = None,
                            assignedToUser: Option[User] = None,
                            assignedToTeam: Option[Team] = None,
                            status: Int,
                            priority: Int,
                            subject: String,
                            description: Option[String] = None)
