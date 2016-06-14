package utils.converters

import database.tables.{ProjectMembersEntity, UserEntity, ContactProfileEntity}
import models.ProjectMembers

object ProjectMemberConverter {
  
  implicit class ProjectMemberEntityWithUsersToProjectMember(tup: List[(ProjectMembersEntity, (UserEntity, ContactProfileEntity))]) {
    import utils.converters.UserConverter._

    def asProjectMemberWithUsers: ProjectMembers = {
      ProjectMembers(
        id = tup.head._1.id,
        projectId = tup.head._1.projectId, 
        members = Some(tup.map(_._2.asUser))
      )
    }
  }

}


