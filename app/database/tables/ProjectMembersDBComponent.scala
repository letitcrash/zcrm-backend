package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ProjectMembersEntity(
  projectId: Int,
  userId: Int)

trait ProjectMembersDBComponent extends DBComponent {
    this: DBComponent 
    with UserDBComponent
    with ProjectDBComponent => 

  import dbConfig.driver.api._

  val projectMembers = TableQuery[ProjectMembersTable]
  
  class ProjectMembersTable(tag: Tag) extends Table[ProjectMembersEntity](tag, "tbl_project_members") {
    def projectId = column[Int]("project_id")
    def userId = column[Int]("user_id")

    def fkUserId = foreignKey("fk_project_members_users", userId, users)(_.id)
    def fkProjectId = foreignKey("fk_project_members_project", projectId, projects)(_.id)

    def * = (projectId, userId)<>(ProjectMembersEntity.tupled, ProjectMembersEntity.unapply)
  }

  def membersWithUsers = projectMembers join usersWithProfile on ( _.userId === _._1.id)

    //CRUD ProjectMembersEntity
  def insertProjectMember(entity: ProjectMembersEntity): Future[ProjectMembersEntity] = {
    db.run(projectMembers += entity).map( res => entity)
  }

  def getProjectMemeberByProjectId(projectId: Int): Future[List[ProjectMembersEntity]] = {
    db.run(projectMembers.filter(_.projectId === projectId).result).map(_.toList)
  }

  def getProjectMembersWithUsersByProjectId(projectId: Int): Future[List[(ProjectMembersEntity,(UserEntity, ContactProfileEntity))]] = {
    db.run(membersWithUsers.filter(_._1.projectId === projectId).result).map(_.toList)
  }

  def deleteAllMembersByProjectId(projectId: Int): Future[Int] = {
    db.run(projectMembers.filter(t => ( t.projectId === projectId)).delete)
  }

  def insertProjectMembers(projectMembers: List[ProjectMembersEntity]): Future[List[ProjectMembersEntity]] = {
    Future.sequence(projectMembers.map( d =>  insertProjectMember(d)))
  }

}

