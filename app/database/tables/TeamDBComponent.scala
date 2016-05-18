package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable

case class TeamEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  description: Option[String] = None,
  recordStatus: String = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

case class TeamGroupEntity(
  teamId: Int,
  userId: Int
)

trait TeamDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent
    with UserDBComponent => 

  import dbConfig.driver.api._

  val teams = TableQuery[TeamTable]
  val teamGroups = TableQuery[TeamGroupTable]
  
  class TeamTable(tag: Tag) extends Table[TeamEntity](tag, "tbl_team") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name")
    def description = column[String]("description", Nullable)
    def recordStatus = column[String]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))

    def fkCompanyId = foreignKey("fk_team_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, companyId, name, description.?, recordStatus, createdAt, updatedAt)<>(TeamEntity.tupled, TeamEntity.unapply)
  }

  class TeamGroupTable(tag: Tag) extends Table[TeamGroupEntity](tag, "tbl_group_team"){
    def teamId = column[Int]("team_id")
    def userId = column[Int]("user_id")

    def fkTeamId = foreignKey("fk_team_id", teamId, teams)(_.id, onUpdate = Restrict, onDelete = Cascade)
    def fkUserId = foreignKey("fk_user_id", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (teamId, userId)<>(TeamGroupEntity.tupled, TeamGroupEntity.unapply)
  }

  //CRUD TeamEntity
  def insertTeam(team: TeamEntity): Future[TeamEntity] = {
      db.run((teams returning teams.map(_.id) into ((team,id) => team.copy(id=Some(id)))) += team)
  }

  def getTeamEntityById(id: Int): Future[TeamEntity] = {
    db.run(teams.filter(t =>(t.id === id && 
                             t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateTeamEntity(team: TeamEntity): Future[TeamEntity] = {
      db.run(teams.filter(t =>(t.id === team.id && 
                                t.recordStatus === RowStatus.ACTIVE)).update(team)
                  .map{num => if(num != 0) team 
                              else throw new Exception("Can't update team, is it deleted?")})
  }

  def softDeleteTeamById(id: Int): Future[TeamEntity] = {
    getTeamEntityById(id).flatMap(res =>
        updateTeamEntity(res.copy(recordStatus = RowStatus.DELETED, 
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getTeamEntitiesByCompanyId(companyId: Int): Future[List[TeamEntity]] = {
    db.run(teams.filter(t => (t.companyId === companyId && 
                              t.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }
}

