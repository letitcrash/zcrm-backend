package utils.converters

import database.tables.{TeamEntity, TeamGroupEntity, EmployeeEntity, UserEntity, ContactProfileEntity}
import models.{Team, TeamGroup, Employee, TeamWithMember}

object TeamConverter {
  
  implicit class EntityToTeam (t: TeamEntity) {
      def asTeam: Team= {
              Team(id = t.id,
                   name = t.name,
                   description = t.description)    
      }
  }

  implicit class TeamWithUserEntityToTeam(tup:(TeamGroupEntity, TeamEntity)) {
    def asTeam: Team = Team(
     id =  tup._2.id,
     name = tup._2.name,
     description = tup._2.description)
  }

  implicit class TeamToEntity(t: Team){
      def asTeamEntity(companyId: Int): TeamEntity = {
              TeamEntity(id = t.id,
                         companyId = companyId,
                         name = t.name,
                         description = t.description)
      }
  }

  implicit class TeamGroupToTeamGroupEntity(t: TeamGroup) {
      def asEntity: TeamGroupEntity = {
        TeamGroupEntity( teamId = t.teamId,
                         userId = t.userId,
                         startDate = t.startDate,
                         endDate = t.endDate)
      }
  }

  implicit class EnitityToTeamGroup(t: TeamGroupEntity) {
      def asTeamGroup: TeamGroup = {
        TeamGroup( teamId = t.teamId,
                   userId = t.userId,
                   startDate = t.startDate,
                   endDate = t.endDate)
      }
  }

  implicit class TeamToTeamGroup(t: Team) {
    def asTeamGroup(userId: Int): TeamGroup = {
      TeamGroup( teamId = t.id.get,
                 userId = userId,
                 startDate = t.startDate,
                 endDate = t.endDate)
    }
  }

  implicit class EmployeeToTeamGroup(e: Employee) {
    def asTeamGroupEntt(teamId: Int): TeamGroupEntity = {
      TeamGroupEntity(teamId = teamId,
                      userId = e.user.get.id.get)
    }
  }

  implicit class TeamWithMemberToTeam(twm: TeamWithMember)  {
    def asTeam: Team = Team(
     id =  twm.id,
     name = twm.name,
     description = twm.description)
  }

  implicit class TeamWithMemberToTeamEntity(t: TeamWithMember)  {
    def asTeamEntity(companyId: Int): TeamEntity = {
            TeamEntity(id = t.id,
                       companyId = companyId,
                       name = t.name,
                       description = t.description)
    }
  }

  implicit class TeamEntityWithEmployeesToTeamWithMember(o: (TeamEntity, List[(TeamGroupEntity, (EmployeeEntity, (UserEntity, ContactProfileEntity)))])) {
    import utils.converters.EmployeeConverter._

    def asTeamWithMember: TeamWithMember = {
      TeamWithMember(
        id = o._1.id,
        name = o._1.name, 
        description = o._1.description,
        members = Some(o._2.map( tup => tup._2.asEmployee))
      )
    }
  }

}


