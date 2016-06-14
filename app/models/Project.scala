package models

case class Project(id: Option[Int] = None,
                   companyId: Int,
                   name: String,
                   members: Option[List[User]] = None,
                   description: Option[String] = None)
