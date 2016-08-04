package database.tables

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class ProjectTeamMemberEntity(
  projectId: Int,
  teamId: Int)

trait ProjectTeamMemberDBComponent extends DBComponent { 
  this: DBComponent 
  with ProjectDBComponent
  with TeamDBComponent => 

  import dbConfig.driver.api._

  val projectTeamMembers = TableQuery[ProjectTeamMemberTable]

  class ProjectTeamMemberTable(tag: Tag) extends Table[ProjectTeamMemberEntity](tag, "tbl_project_team_member") {
    def projectId = column[Int]("project_id")
    def teamId = column[Int]("team_id")

    def fkProject = foreignKey("fk_project_team_member_project", projectId, projects)(_.id)
    def fkTeam = foreignKey("fk_project_team_member_team", teamId, teams)(_.id)
    override def * = (projectId, teamId) <> (ProjectTeamMemberEntity.tupled, ProjectTeamMemberEntity.unapply)
  }
  
  //JOINS 

  //(ProjectTeamMemberEntity , TeamEntity)
  def projectTeamMembersWithTeam = projectTeamMembers join teams on (_.teamId === _.id) 

  //CRUD
  def insertProjectTeamMember(entity: ProjectTeamMemberEntity): Future[ProjectTeamMemberEntity] = {
    db.run(projectTeamMembers += entity).map( res => entity)
  }

  def deleteProjectTeamMember(entity: ProjectTeamMemberEntity): Future[ProjectTeamMemberEntity] = {
    db.run(projectTeamMembers.filter( t => (t.projectId === entity.projectId &&
                                           t.teamId === entity.teamId)).delete)
    Future(entity)
  }

  //FILTERS 
  def insertProjectTeamMembers(projectTeamMemberList: List[ProjectTeamMemberEntity]): Future[List[ProjectTeamMemberEntity]] = {
    Future.sequence(projectTeamMemberList.map( d =>  insertProjectTeamMember(d)))
  }

  def deleteProjectTeamMembers(projectTeamMemberList : List[ProjectTeamMemberEntity]): Future[List[ProjectTeamMemberEntity]] = {
    Future.sequence(projectTeamMemberList.map( t =>  deleteProjectTeamMember(t)))
    Future(projectTeamMemberList)
  }

  def deleteAllTeamsByProjectId(projectId: Int): Future[Int] = {
    db.run(projectTeamMembers.filter(t => ( t.projectId === projectId)).delete)
  }

  def getTeamsByProjectId(projectId: Int): Future[List[(ProjectTeamMemberEntity , TeamEntity)]] = {
    db.run(projectTeamMembersWithTeam.filter(_._1.projectId === projectId ).result).map(_.toList)
  }
  

}
