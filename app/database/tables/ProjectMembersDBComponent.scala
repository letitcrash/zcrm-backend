package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ProjectMembersEntity(
  id: Option[Int] = None,
  projectId: Int,
  userId: Int)

trait ProjectMembersDBComponent extends DBComponent {
    this: DBComponent 
    with UserDBComponent
    with ProjectDBComponent => 

  import dbConfig.driver.api._

  val projectMembers = TableQuery[ProjectMembersTable]
  
  class ProjectMembersTable(tag: Tag) extends Table[ProjectMembersEntity](tag, "tbl_projectMembers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def projectId = column[Int]("project_id")
    def userId = column[Int]("user_id")

    def fkUserId = foreignKey("fk_project_members_users", userId, users)(_.id)
    def fkProjectId = foreignKey("fk_project_members_project", projectId, projects)(_.id)

    def * = (id.?, projectId, userId)<>(ProjectMembersEntity.tupled, ProjectMembersEntity.unapply)
  }

  def membersWithUsers = projectMembers join usersWithProfile on ( _.userId === _._1.id)

    //CRUD ProjectMembersEntity
  def insertProjectMember(projectMember: ProjectMembersEntity): Future[ProjectMembersEntity] = {
      db.run((projectMembers returning projectMembers.map(_.id) into ((projectMembers,id) => projectMembers.copy(id=Some(id)))) += projectMember)
  }

  def getProjectMemberEntityById(id: Int): Future[ProjectMembersEntity] = {
    db.run(projectMembers.filter(_.id === id).result.head)
  }

  def getProjectMemeberByProjectId(projectId: Int): Future[List[ProjectMembersEntity]] = {
    db.run(projectMembers.filter(_.projectId === projectId).result).map(_.toList)
  }

  def getProjectMembersWithUsersByProjectId(projectId: Int): Future[List[(ProjectMembersEntity,(UserEntity, ContactProfileEntity))]] = {
    db.run(membersWithUsers.filter(_._1.projectId === projectId).result).map(_.toList)
  }

  def deleteProjectMembersById(id: Int): Future[ProjectMembersEntity] = {
    val deleted = getProjectMemberEntityById(id)
    db.run(projectMembers.filter(_.id === id).delete)
    deleted    
  } 

}

