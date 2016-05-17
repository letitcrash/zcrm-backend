package utils.converters

import database.tables.TeamEntity
import models.Team

object TeamConverter {
  
  implicit class EntityToTeam (t: TeamEntity) {
  		def asTeam: Team= {
      				Team(id = t.id,
							 		 name = t.name,
							 		 description = t.description)    
			}
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


