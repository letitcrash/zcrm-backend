package models

case class TeamWithMember(
  id: Option[Int] = None,
  name: String,
  description: Option[String] = None,
  members: Option[List[Employee]])
