package utils.converters

import database.tables.{ProjectMembersEntity, UserEntity, ContactProfileEntity, ProjectTeamMemberEntity}
import models.ProjectMembers

object ProjectMemberConverter {
  
  implicit class ProjectMemberEntityWithUsersToProjectMember(tup: List[(ProjectMembersEntity, (UserEntity, ContactProfileEntity))]) {
    import utils.converters.UserConverter._

    def asProjectMemberWithUsers: ProjectMembers = {
      ProjectMembers(
        projectId = tup.head._1.projectId, 
        members = Some(tup.map(_._2.asUser))
      )
    }
  }


  implicit class TupMemberToEntity(t: (Int, Int)) {
    def asProjectMemberEntt(): ProjectMembersEntity = {
      ProjectMembersEntity(t._1, t._2)
    }
  }

  implicit class TupTeamMemberToEntity(t: (Int, Int)) {
    def asProjectTeamMemberEntt(): ProjectTeamMemberEntity = {
      ProjectTeamMemberEntity(t._1, t._2)
    }
  }

}


