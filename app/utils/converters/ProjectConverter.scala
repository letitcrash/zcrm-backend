package utils.converters

import database.tables.ProjectEntity
import models.Project

object ProjectConverter {
  
  implicit class EntityToProject (p: ProjectEntity) {
      def asProject: Project = {
              Project(id = p.id,
                      name = p.name,
                      description = p.description)    
      }
  }

  implicit class ProjectToEntity(p: Project){
      def asProjectEntity(companyId: Int): ProjectEntity = {
              ProjectEntity(id = p.id,
                            companyId = companyId,
                            name = p.name,
                            description = p.description) 
      }
  }
}


