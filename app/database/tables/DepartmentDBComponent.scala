package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable

case class DepartmentEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  recordStatus: String = RowStatus.ACTIVE,
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
    def name = column[String]("name")
    def recordStatus = column[String]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))

    def fkCompanyId = foreignKey("fk_department_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, companyId, name, recordStatus, createdAt, updatedAt)<>(DepartmentEntity.tupled, DepartmentEntity.unapply)
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
}

