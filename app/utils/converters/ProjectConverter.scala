package utils.converters

import database.tables.{ProjectEntity, UserEntity, ContactProfileEntity}
import models.{Project, User}

object ProjectConverter {
  
  implicit class EntityToProject (p: ProjectEntity) {
      def asProject(members: Option[List[User]] = None, 
                    countNew: Option[Int] = None,
                    countOpen: Option[Int] = None,
                    countPostponed: Option[Int] = None,
                    countResolved: Option[Int] = None): Project = {
              Project(id = p.id,
                      companyId = p.companyId,
                      name = p.name,
                      members = members,
                      description = p.description,
                      countNew = countNew,
                      countOpen = countOpen,
                      countPostponed = countPostponed,
                      countResolved = countResolved)     
      }

      def asSimpleProject = {
              Project(id = p.id,
                      companyId = p.companyId,
                      name = p.name)
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


