package utils.converters

import database.tables.UnionEntity
import models.Union

object UnionConverter {
  
  implicit class EntityToUnion (u: UnionEntity) {
  		def asUnion: Union = {
      				Union(id = u.id,
							 		 name = u.name,
							 		 description = u.description)    
			}
  }

	implicit class UnionToEntity(u: Union){
			def asUnionEntity(companyId: Int): UnionEntity = {
							UnionEntity(id = u.id,
												 companyId = companyId,
												 name = u.name,
												 description = u.description)
			}
	}
}


