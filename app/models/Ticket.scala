package models
import java.sql.Timestamp

case class Ticket(id: Option[String] = None,
                  companyId: Int,
                  projectId: Option[Int] = None, //should be deleted later 
                  project: Option[Project] = None, 
                  createdByUserId: Int, //should be deleted later 
                  createdByUser: Option[User] = None,
                  //requestedByUserId: Option[Int] = None,
                  members: Option[List[User]] = None, 
                  teams: Option[List[Team]] = None,
                  clients: Option[List[Client]] = None, 
                  requesters: Option[List[User]] = None, 
                  //requestedByEmail: Option[String] = None,
                  //assignedToUserId: Option[Int] = None,
                  //assignedToTeamId: Option[Int] = None,
                  status: Int,
                  priority: Int,
                  subject: String,
                  description: Option[String] = None,
                  createdAt: Option[Timestamp] = None)
