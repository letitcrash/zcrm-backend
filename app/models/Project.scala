package models

case class Project(id: Option[Int] = None,
                   companyId: Int,
                   name: String,
                   description: Option[String] = None)
