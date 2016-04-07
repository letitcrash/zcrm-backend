package utils.converters

import database.tables.{CompanyEntity, ContactProfileEntity, EmployeeEntity, UserEntity}
import models.{Employee, User}

object EmployeeConverter {


  implicit class EntityUserWithProfile
  (tup: (EmployeeEntity, (UserEntity, ContactProfileEntity))) {

    def asEmployee(): Employee = {
      import UserConverter.EntityToUser
      Employee(
        id = tup._1.id,
        user = Option(tup._2.asUser),
        companyId = tup._1.companyId,
        employeeType = tup._1.employeeType,
        employeeLevel = tup._1.employeeLevel)
    }
  }

  implicit class EmployeeToEmployeeEntity(o: Employee) {
    def asEmployeeEntity: EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = o.companyId,
        userId = Some(o.user.get.id.get),
        employeeType = o.employeeType,
        employeeLevel = o.employeeLevel)
    }

    def asEmployeeEntity(companyId: Int, userId: Int): EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = companyId,
        userId = Some(userId),
        employeeType = o.employeeType,
        employeeLevel = o.employeeLevel)
    }
  }

  implicit class EmployeeEntityToEmployee
  (o: (EmployeeEntity, UserEntity, ContactProfileEntity)) {

    def asEmployee() = {
      import UserConverter.EntityToUser

      val emp = o._1
      Employee(
        id = emp.id,
        user = Some((o._2, o._3).asUser),
        companyId = o._1.companyId,
        employeeType = o._1.employeeType,
        employeeLevel = o._1.employeeLevel)
    }
  }

  def toEmployees(ents: List[(EmployeeEntity, CompanyEntity)], user: User): List[Employee] = {
    for {
      (emp, comp) <- ents
    } yield Employee(
        id = emp.id,
        user = Some(user),
        companyId = comp.id.getOrElse(0),
        employeeType = emp.employeeType,
        employeeLevel = emp.employeeLevel)
  }
}
