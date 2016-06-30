package utils.converters

import database.tables.{ProjectEntity, UserEntity, ContactProfileEntity}
import models.{Project, User, Client}

object ProjectConverter {
  
  implicit class EntityToProject (p: ProjectEntity) {
      def asProject(members: Option[List[User]] = None, 
                    countNew: Option[Int] = None,
                    countOpen: Option[Int] = None,
                    countPostponed: Option[Int] = None,
                    countResolved: Option[Int] = None,
                    clients: Option[List[Client]] = None): Project = {
              Project(id = p.id,
                      companyId = p.companyId,
                      name = p.name,
                      members = members,
                      clients = clients,
                      description = p.description,
                      createdAt = Some(p.createdAt),
                      deadline = p.deadline,
                      countNew = countNew,
                      countOpen = countOpen,
                      countPostponed = countPostponed,
                      countResolved = countResolved)     
      }

      def asSimpleProject = {
              Project(id = p.id,
                      companyId = p.companyId,
                      name = p.name,
                      createdAt = Some(p.createdAt),
                      deadline = p.deadline)
      }
  }

  implicit class ProjectToEntity(p: Project){
      def asProjectEntity: ProjectEntity = {
              ProjectEntity(id = p.id,
                            companyId = p.companyId,
                            name = p.name,
                            description = p.description,
                            deadline = p.deadline) 
      }
  }
}


