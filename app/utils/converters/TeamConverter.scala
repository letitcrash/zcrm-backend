package utils.converters

import database.tables.{TeamEntity, TeamGroupEntity}
import models.Team

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
}


