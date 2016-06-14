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
  name: String,
  description: Option[String] = None,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

case class ProjectMembersGroupEntity(
  projectMembersId: Int,
  userId: Int
)

trait ProjectMembersDBComponent extends DBComponent {
    this: DBComponent 
    with ProjectDBComponent
    with EmployeeDBComponent
    with UserDBComponent => 

  import dbConfig.driver.api._

  val projectMembers = TableQuery[ProjectMembersTable]
  val projectMembersGroups = TableQuery[ProjectMembersGroupTable]
  
  class ProjectMembersTable(tag: Tag) extends Table[ProjectMembersEntity](tag, "tbl_project_members") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def description = column[String]("description", Nullable, O.SqlType("VARCHAR(255)"))
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at")

    def * = (id.?, name, description.?, recordStatus, createdAt, updatedAt)<>(ProjectMembersEntity.tupled, ProjectMembersEntity.unapply)
  }

  class ProjectMembersGroupTable(tag: Tag) extends Table[ProjectMembersGroupEntity](tag, "tbl_group_project_members"){
    def projectMembersId = column[Int]("project_members_id")
    def userId = column[Int]("user_id")

    def fkProjectMembersId = foreignKey("fk_project_members_id", projectMembersId, projectMembers)(_.id, onUpdate = Restrict, onDelete = Cascade)
    def fkUserId = foreignKey("fk_project_members_user_id", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (projectMembersId, userId)<>(ProjectMembersGroupEntity.tupled, ProjectMembersGroupEntity.unapply)
  }

  def groupWithProjectMembers = projectMembersGroups join projectMembers on ( _.projectMembersId === _.id)
  // (ProjectMembersGroupEntity, (EmployeeEntity, (UserEntity, ContactProfile)))
  def projectMembersWithEmployee = projectMembersGroups join employeesWithUsersWihtProfile on ( _.userId === _._1.userId)

  //CRUD ProjectMembersEntity
  def insertProjectMembers(projectMember: ProjectMembersEntity): Future[ProjectMembersEntity] = {
      db.run((projectMembers returning projectMembers.map(_.id) into ((projectMember,id) => projectMember.copy(id=Some(id)))) += projectMember)
  }

  def getProjectMembersEntityById(id: Int): Future[ProjectMembersEntity] = {
    db.run(projectMembers.filter(t =>(t.id === id && 
                             t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateProjectMembersEntity(projectMember: ProjectMembersEntity): Future[ProjectMembersEntity] = {
      db.run(projectMembers.filter(t =>(t.id === projectMember.id && 
                                t.recordStatus === RowStatus.ACTIVE)).update(projectMember)
                  .map{num => if(num != 0) projectMember 
                              else throw new Exception("Can't update projectMembers, is it deleted?")})
  }

  def softDeleteProjectMembersById(id: Int): Future[ProjectMembersEntity] = {
    getProjectMembersEntityById(id).flatMap(res =>
        updateProjectMembersEntity(res.copy(recordStatus = RowStatus.DELETED, 
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //CRUD ProjectMembersGroup 
  def insertProjectMembersGroup(projectMembersGroup: ProjectMembersGroupEntity): Future[ProjectMembersGroupEntity] = {
      db.run(projectMembersGroups += projectMembersGroup).map( res => projectMembersGroup)
  }

  def deleteProjectMembersGroup(projectMembersGroup: ProjectMembersGroupEntity): Future[ProjectMembersGroupEntity] = {
    db.run(projectMembersGroups.filter( t => ( t.projectMembersId === projectMembersGroup.projectMembersId &&
                                t.userId === projectMembersGroup.userId)).delete)
    Future(projectMembersGroup)
  }
  
  def getProjectMembersGroupEntityByUserId(userId: Int): Future[ProjectMembersGroupEntity] = {
    db.run(projectMembersGroups.filter(_.userId === userId).result.head) 
  }

  def deleteProjectMembersGroupByUserId(userId: Int): Future[ProjectMembersGroupEntity] = {
    val lastDeleted = getProjectMembersGroupEntityByUserId(userId)
    db.run(projectMembersGroups.filter(_.userId === userId).delete)
    lastDeleted
  }

  //FILTERS
  def insertProjectMembersGroups(projectMembersGroup: List[ProjectMembersGroupEntity]): Future[List[ProjectMembersGroupEntity]] = {
    Future.sequence(projectMembersGroup.map( t =>  insertProjectMembersGroup(t)))
  }

  def getProjectMembersEntitiesByUserId(userId: Int): Future[List[(ProjectMembersGroupEntity, ProjectMembersEntity)]] = {
    db.run(groupWithProjectMembers.filter( _._1.userId === userId ).result).map(_.toList)
  }

  def getProjectMembersEmployeesByProjectMembersId(projectMembersId: Int): Future[List[(ProjectMembersGroupEntity, (EmployeeEntity, (UserEntity, ContactProfileEntity)))]] = {
    db.run(projectMembersWithEmployee.filter( _._1.projectMembersId === projectMembersId ).result).map(_.toList)
  }

  def deleteProjectMembersGroups(projectMembersGroup: List[ProjectMembersGroupEntity]): Future[List[ProjectMembersGroupEntity]] = {
    Future.sequence(projectMembersGroup.map( t =>  deleteProjectMembersGroup(t)))
    Future(projectMembersGroup)
  }

  def searchProjectMembersEntitiesByName(pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[ProjectMembersEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        projectMembers.filter{_.name.like(s)}
      }.getOrElse(projectMembers)  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( projectMembersList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = projectMembersList)
          )
        )
  }


}

