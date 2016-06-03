package models

case class TeamWithMember(team: Team, members: Option[List[Employee]])
