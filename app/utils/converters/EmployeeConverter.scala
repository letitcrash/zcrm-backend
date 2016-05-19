package utils.converters

import database.tables._
import models.{Employee, User}

object EmployeeConverter {

  implicit class AggregatedEmployeeEnttToEmployee
  (tup: (((((EmployeeEntity,  (UserEntity, ContactProfileEntity)), PositionEntity) , ShiftEntity),  DepartmentEntity), UnionEntity) ) {
    def asEmployee(): Employee = {
      val employeeTup = tup._1._1._1._1._1 
      val userTup = tup._1._1._1._1._2
      val positonTup =  tup._1._1._1._2
      val shiftTup =  tup._1._1._2
      val departmentTup = tup._1._2
      val unionTup = tup._2
      import UserConverter.EntityToUser
      Employee(
        id = employeeTup.id,
        user = Some(userTup.asUser),
        companyId = employeeTup.companyId,
        employeeLevel = employeeTup.companyId
      )
    }
  }

  implicit class EntityUserWithProfile
  (tup: (EmployeeEntity, (UserEntity, ContactProfileEntity))) {

    def asEmployee(): Employee = {
      import UserConverter.EntityToUser
      Employee(
        id = tup._1.id,
        user = Some(tup._2.asUser),
        companyId = tup._1.companyId,
        //employeeType = tup._1.employeeType,
        employeeLevel = tup._1.employeeLevel)
    }
  }

  implicit class EmployeeToEmployeeEntity(o: Employee) {
    def asEmployeeEntity: EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = o.companyId,
        userId = o.user.get.id.get,
        //employeeType = o.employeeType,
        employeeLevel = o.employeeLevel)
    }

    def asEmployeeEntity(companyId: Int, userId: Int): EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = companyId,
        userId = userId,
        //employeeType = o.employeeType,
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
        //employeeType = o._1.employeeType,
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
        //employeeType = emp.employeeType,
        employeeLevel = emp.employeeLevel)
  }
}
