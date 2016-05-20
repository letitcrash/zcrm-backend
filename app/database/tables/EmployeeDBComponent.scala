package database.tables

import models.{UserLevels, RowStatus}
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable



case class EmployeeEntity(
                           id: Option[Int],
                           companyId:  Int,
                           userId: Int,
                           positionId: Option[Int] = None,
                           shiftId: Option[Int] = None, 
                           departmentId: Option[Int] = None, 
                           unionId: Option[Int] = None,

                           //employeeType: Option[String],

                           // The level the user has within a company, i.e admin or normal employee
                           // 1000 - 9999  = user level
                           // 100 - 999    = Human resource
                           // 0-99         = Admin levels
                           employeeLevel: Int,
                           recordStatus: String = RowStatus.ACTIVE)



trait EmployeeDBComponent extends DBComponent{
  this: DBComponent 
    with UserDBComponent
    with ContactProfileDBComponent
    with DepartmentDBComponent
    with PositionDBComponent
    with ShiftDBComponent
    with UnionDBComponent
    with CompanyDBComponent =>


  import dbConfig.driver.api._

  val employees = TableQuery[EmployeeTable]

  class EmployeeTable(tag: Tag) extends Table[EmployeeEntity](tag, "tbl_employee") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def userId = column[Int]("user_id")
    def positionId = column[Int]("position_id", Nullable)
    def shiftId = column[Int]("shift_id", Nullable)
    def departmentId = column[Int]("department_id", Nullable)
    def unionId = column[Int]("union_id", Nullable)
   // def employeeType = column[String]("employee_type", Nullable)
    def comment = column[String]("comment")
    def employeeLevel = column[Int]("employee_level", O.Default(UserLevels.USER))
    def recordStatus = column[String]("record_status", O.Default(RowStatus.ACTIVE))


    def fkEmployeeUser =
      foreignKey("fk_employee_user", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def fkEmployeeCompany =
      foreignKey("fk_employee_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)

    def fkEmployeePosition = 
      foreignKey("fk_employee_position", positionId, positions)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)

    def fkEmployeeShift = 
      foreignKey("fk_employee_shift", shiftId, shifts)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)

    def fkEmployeeDepartment = 
      foreignKey("fk_employee_department", departmentId, departments)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)

    def fkEmployeeUnion = 
      foreignKey("fk_employee_union", unionId, unions)(_.id, onUpdate = Restrict, onDelete = ForeignKeyAction.Cascade)

    override def * =
      ( id.?, companyId, userId, positionId.?, shiftId.?, departmentId.?, unionId.?,  employeeLevel, recordStatus) <> (EmployeeEntity.tupled, EmployeeEntity.unapply)
  }

  //(EmployeeEntity,  (UserEntity, ContactProfileEntity))
  def employeesWithUsersWihtProfile = employees join usersWithProfile on (_.userId === _._1.id)

  //((((EmployeeEntity,  (UserEntity, ContactProfileEntity)), PositionEntity) , ShiftEntity),  DepartmentEntity), UnionEntity )
  def aggregatedEmployee = employeesWithUsersWihtProfile joinLeft
                             positions on (_._1.positionId === _.id) joinLeft
                             shifts on ( _._1._1.shiftId === _.id) joinLeft
                             departments on ( _._1._1._1.departmentId === _.id) joinLeft
                             unions on ( _._1._1._1._1.unionId === _.id) 


  //CRUD EmployeeEntity
  def insertEmployee(empl: EmployeeEntity): Future[EmployeeEntity] = {
    db.run((employees returning employees.map(_.id)
                  into ((empl,id) => empl.copy(id=Some(id)))) += empl)
  }

  def getEmployeeById(id: Int): Future[EmployeeEntity] = {
    db.run(employees.filter(t => (t.id === id &&
                                  t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getEmployeeByUserId(userId: Int): Future[EmployeeEntity] = {
    db.run(employees.filter(t => (t.userId === userId &&
                                  t.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateEmployeeEntity(newEmpl: EmployeeEntity): Future[EmployeeEntity] = {
    db.run(employees.filter(_.id === newEmpl.id).update(newEmpl))
                    .map( num => newEmpl)
  }

  def softDeleteEmployeeEntityByUserId(userId: Int): Future[EmployeeEntity] = {
    getEmployeeByUserId(userId).flatMap(empl =>
          updateEmployeeEntity(empl.copy(recordStatus = RowStatus.DELETED)))
  }

  def softDeleteEmployeeEntityById(id: Int): Future[EmployeeEntity] = {
    getEmployeeById(id).flatMap(empl =>
          updateEmployeeEntity(empl.copy(recordStatus = RowStatus.DELETED)))
  }

  //EmployeeEntity filters
  def upsertEmployee(empl: EmployeeEntity): Future[EmployeeEntity] = {
    if(empl.id.isDefined) {updateEmployeeEntity(empl)}
    else {insertEmployee(empl)}
  }

  def getAllEmployeesWithUsersByCompanyId(companyId: Int): Future[List[(EmployeeEntity,  (UserEntity, ContactProfileEntity))]] = {
    db.run(employeesWithUsersWihtProfile.filter(t =>(t._1.companyId === companyId && 
                                                     t._1.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getAllAggregatedEmployeesByCompanyId(companyId: Int)
   : Future[List[(((((EmployeeEntity,  (UserEntity, ContactProfileEntity)), Option[PositionEntity]) , Option[ShiftEntity]),  Option[DepartmentEntity]), Option[UnionEntity])]] = {
    db.run(aggregatedEmployee.filter(t => (t._1._1._1._1._1.companyId === 1  &&
                                           t._1._1._1._1._1.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def getEmployeeWithUserById(employeeId: Int): Future[(EmployeeEntity,  (UserEntity, ContactProfileEntity))] = {
    db.run(employeesWithUsersWihtProfile.filter(t =>(t._1.id === employeeId && 
                                                     t._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def getEmployeeWithUserByUserId(userId: Int): Future[(EmployeeEntity,  (UserEntity, ContactProfileEntity))] = {
    db.run(employeesWithUsersWihtProfile.filter(t =>(t._1.userId === userId && 
                                                     t._1.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateEmployeeWithUser(emplEntt: EmployeeEntity): Future[(EmployeeEntity,  (UserEntity, ContactProfileEntity))] = {
    updateEmployeeEntity(emplEntt).flatMap(updatedEmpl =>
        getEmployeeWithUserById(updatedEmpl.id.get))
  }

}
