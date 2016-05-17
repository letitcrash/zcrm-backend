package models

case class Team(id: Option[Int] = None,
								name: String,
								description: Option[String] = None)
