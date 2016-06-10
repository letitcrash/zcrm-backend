package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ProjectEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  description: Option[String] = None,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait ProjectDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent => 

  import dbConfig.driver.api._

  val projects = TableQuery[ProjectTable]
  
  class ProjectTable(tag: Tag) extends Table[ProjectEntity](tag, "tbl_project") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def description = column[String]("description", O.SqlType("VARCHAR(255)"), Nullable)
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at")

    def fkCompanyId = foreignKey("fk_project_company", companyId, companies)(_.id)

    def * = (id.?, companyId, name, description.?, recordStatus, createdAt, updatedAt)<>(ProjectEntity.tupled, ProjectEntity.unapply)
  }


  def projectQry(companyId: Int) = {
    projects.filter(p =>(p.companyId === companyId && 
                         p.recordStatus === RowStatus.ACTIVE))
  }

  //CRUD ProjectEntity
  def insertProject(project: ProjectEntity): Future[ProjectEntity] = {
      db.run((projects returning projects.map(_.id) into ((project,id) => project.copy(id=Some(id)))) += project)
  }

  def getProjectEntityById(id: Int): Future[ProjectEntity] = {
    db.run(projects.filter(p =>(p.id === id && 
                                p.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateProjectEntity(project: ProjectEntity): Future[ProjectEntity] = {
      db.run(projects.filter(p =>(p.id === project.id && 
                                  p.recordStatus === RowStatus.ACTIVE)).update(project)
                    .map{num => if(num != 0) project 
                                else throw new Exception("Can't update project, is it deleted?")})
  }

  def softDeleteProjectById(id: Int): Future[ProjectEntity] = {
    getProjectEntityById(id).flatMap(res =>
        updateProjectEntity(res.copy(recordStatus = RowStatus.DELETED, 
                                   updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getProjectEntitiesByCompanyId(companyId: Int): Future[List[ProjectEntity]] = {
    db.run(projects.filter(p => (p.companyId === companyId && 
                                 p.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def searchProjectEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[ProjectEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        projectQry(companyId).filter{_.name.like(s)}
      }.getOrElse(projectQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( projectList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = projectList)
          )
        )
  }
}

