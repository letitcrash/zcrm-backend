package models

case class ProjectMembers(id: Option[Int] = None,
                           projectId: Int,
                           members: Option[List[User]] = None)
