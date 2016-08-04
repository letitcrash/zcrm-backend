package models
import java.sql.Timestamp

case class Project(id: Option[Int] = None,
                   companyId: Int,
                   name: String,
                   members: Option[List[User]] = None,
                   clients: Option[List[Client]] = None,
                   description: Option[String] = None,
                   createdAt: Option[Timestamp] = None,
                   deadline: Option[Timestamp] = None,
                   countNew: Option[Int] = None,
                   countOpen: Option[Int] = None,
                   countPostponed: Option[Int] = None,
                   countResolved: Option[Int] = None)
