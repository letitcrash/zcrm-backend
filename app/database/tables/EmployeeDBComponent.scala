package database.tables

import models.UserLevels
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext



case class EmployeeEntity(
                           id: Option[Int],
                           companyId:  Int,
                           userId: Option[Int],
                           employeeType: Option[String],
                           employmentNumberType: Option[String],
                           employmentNumberValue: Option[String],
                           isSubContractor: Boolean,
                           subContractorOrgNr: Option[String],
                           subContractorCompanyName: Option[String],
                           comment: Option[String],
                           // The level the user has within a company, i.e admin or normal employee
                           // 1000 - 9999  = user level
                           // 100 - 999    = Human resource
                           // 0-99         = Admin levels
                           employeeLevel: Int,
                           recordStatus: Int = 1)



trait EmployeeDBComponent extends DBComponent
    with UserDBComponent
    with ContactProfileDBComponent
    with CompanyDBComponent {

  this: DBComponent =>

  import dbConfig.driver.api._

  val employees = TableQuery[EmployeeTable]

  class EmployeeTable(tag: Tag) extends Table[EmployeeEntity](tag, "tbl_employee") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def userId = column[Int]("user_id")
    def employeeType = column[String]("employee_type")
    def employmentNumberType = column[String]("employment_number_type")
    def employmentNumberValue = column[String]("employment_number_value")
    def isSubContractor = column[Boolean]("employee_is_subcontractor")
    def subContractorOrgNr = column[String]("subcontractor_org_nr")
    def subContractorCompanyName = column[String]("subcontractor_company_name")
    def comment = column[String]("comment")
    def employeeLevel = column[Int]("employee_level", O.Default(UserLevels.USER))
    def recordStatus = column[Int]("record_status", O.Default(1))


    def fkEmployeeUser =
      foreignKey("fk_employee_user", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def fkEmployeeCompany =
      foreignKey("fk_employee_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)


    override def * =
      ( id.?,
        companyId,
        userId.?,
        employeeType.?,
        employmentNumberType.?,
        employmentNumberValue.?,
        isSubContractor,
        subContractorOrgNr.?,
        subContractorCompanyName.?,
        comment.?,
        employeeLevel,
        recordStatus) <>
        (EmployeeEntity.tupled, EmployeeEntity.unapply)
  }
  //CRUD EmployeeEntity
  def insertEmployee(empl: EmployeeEntity): Future[EmployeeEntity] = {
    db.run((employees returning employees.map(_.id)
                  into ((empl,id) => empl.copy(id=Some(id)))) += empl)
  }

  def updateEmployee(newEmpl: EmployeeEntity): Future[EmployeeEntity] = {
      db.run(employees.filter(_.id === newEmpl.id).update(newEmpl))
                    .map( num => newEmpl)
  }

  //EmployeeEntity filters
  def upsertEmployee(empl: EmployeeEntity): Future[EmployeeEntity] = {
    if(empl.id.isDefined) {updateEmployee(empl)}
    else {insertEmployee(empl)}
  }
}
