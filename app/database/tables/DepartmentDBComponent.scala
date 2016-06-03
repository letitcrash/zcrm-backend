package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class DepartmentEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

case class DepartmentGroupEntity(
  departmentId: Int,
  userId: Int
)

trait DepartmentDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent => 

  import dbConfig.driver.api._

  val departments = TableQuery[DepartmentTable]
  
  class DepartmentTable(tag: Tag) extends Table[DepartmentEntity](tag, "tbl_department") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null"))

    def fkCompanyId = foreignKey("fk_department_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, companyId, name, recordStatus, createdAt, updatedAt)<>(DepartmentEntity.tupled, DepartmentEntity.unapply)
  }


  def departmentQry(companyId: Int) = {
    departments.filter(d =>(d.companyId === companyId && 
                            d.recordStatus === RowStatus.ACTIVE))
  }
  //CRUD DepartmentEntity
  def insertDepartment(department: DepartmentEntity): Future[DepartmentEntity] = {
      db.run((departments returning departments.map(_.id) into ((department,id) => department.copy(id=Some(id)))) += department)
  }

  def getDepartmentEntityById(id: Int): Future[DepartmentEntity] = {
    db.run(departments.filter(d =>(d.id === id && 
                              d.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateDepartmentEntity(department: DepartmentEntity): Future[DepartmentEntity] = {
      db.run(departments.filter(d =>(d.id === department.id && 
                                     d.recordStatus === RowStatus.ACTIVE)).update(department)
                  .map{num => if(num != 0) department 
                              else throw new Exception("Can't update department, is it deleted?")})
  }

  def softDeleteDepartmentById(id: Int): Future[DepartmentEntity] = {
    getDepartmentEntityById(id).flatMap(res =>
        updateDepartmentEntity(res.copy(recordStatus = RowStatus.DELETED, 
                            updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getDepartmentEntitiesByCompanyId(companyId: Int): Future[List[DepartmentEntity]] = {
    db.run(departments.filter(d => (d.companyId === companyId && 
                              d.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def searchDepartmentEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[DepartmentEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        departmentQry(companyId).filter{_.name.like(s)}
      }.getOrElse(departmentQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( departmentList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = departmentList)
          )
        )
  }
}

