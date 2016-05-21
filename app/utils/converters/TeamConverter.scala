package utils.converters

import database.tables.{TeamEntity, TeamGroupEntity}
import models.{Team, TeamGroup}

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
                         userId = t.userId)
      }
  }

  implicit class EnitityToTeamGroup(t: TeamGroupEntity) {
      def asTeamGroup: TeamGroup = {
        TeamGroup( teamId = t.teamId,
                   userId = t.userId)
      }
  }

  implicit class TeamToTeamGroup(t: Team) {
    def asTeamGroup(userId: Int): TeamGroup = {
      TeamGroup( teamId = t.id.get,
                 userId = userId)
    }
  }

}


