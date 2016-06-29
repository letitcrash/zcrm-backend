package models

case class Project(id: Option[Int] = None,
                   companyId: Int,
                   name: String,
                   members: Option[List[User]] = None,
                   clients: Option[List[Client]] = None,
                   description: Option[String] = None,
                   countNew: Option[Int] = None,
                   countOpen: Option[Int] = None,
                   countPostponed: Option[Int] = None,
                   countResolved: Option[Int] = None)
