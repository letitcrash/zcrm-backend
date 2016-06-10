package models

case class Project(id: Option[Int] = None,
                   name: String,
                   description: Option[String] = None)
