package utils.converters

import database.tables.{ProjectEntity, UserEntity, ContactProfileEntity}
import models.{Project, User}

object ProjectConverter {
  
  implicit class EntityToProject (p: ProjectEntity) {
      def asProject(members: Option[List[User]] = None): Project = {
              Project(id = p.id,
                      companyId = p.companyId,
                      name = p.name,
                      members = members,
                      description = p.description)     
      }
  }

  implicit class ProjectToEntity(p: Project){
      def asProjectEntity: ProjectEntity = {
              ProjectEntity(id = p.id,
                            companyId = p.companyId,
                            name = p.name,
                            description = p.description) 
      }
  }
}


