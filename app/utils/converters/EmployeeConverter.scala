package utils.converters

import database.tables._
import models.{Employee, User}

object EmployeeConverter {

  implicit class AggregatedEmployeeEnttToEmployee
  (tup: (((((EmployeeEntity,  (UserEntity, ContactProfileEntity)), Option[PositionEntity]) , Option[ShiftEntity]),  Option[DepartmentEntity]), Option[UnionEntity]) ) {
    def asEmployee(teamEntts: List[(TeamGroupEntity, TeamEntity)], delegateEnnts: List[(GroupDelegateEntity, DelegateEntity)]): Employee = {
      import UserConverter.EntityToUser
      import DelegateConverter._
      import ShiftConverter._
      import DepartmentConverter._
      import UnionConverter._
      import TeamConverter._
      import PositionConverter._
      val employeeTup = tup._1._1._1._1._1 
      val userTup = tup._1._1._1._1._2
      val positionTup =  tup._1._1._1._2
      val shiftTup =  tup._1._1._2
      val departmentTup = tup._1._2
      val unionTup = tup._2
      Employee(
        id = employeeTup.id,
        user = Some(userTup.asUser),
        companyId = employeeTup.companyId,
        position = positionTup.map(_.asPosition),
        shift = shiftTup.map(_.asShift),
        department = departmentTup.map(_.asDepartment),
        union = unionTup.map(_.asUnion),
        teams = Some(teamEntts.map(_.asTeam)),
        delegates = Some(delegateEnnts.map(_.asDelegate)),
        employeeLevel = employeeTup.employeeLevel
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
        employeeLevel = tup._1.employeeLevel)
    }
  }

  implicit class EmployeeToEmployeeEntity(o: Employee) {
    def asEmployeeEntity: EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = o.companyId,
        userId = o.user.get.id.get,
        positionId = o.position match { case Some(p) => p.id; case _ => None },
        shiftId = o.shift match { case Some(s) => s.id; case _ => None}, 
        departmentId = o.department match { case Some(d) => d.id; case _ => None},
        unionId = o.union match { case Some(u) => u.id; case _ => None},
        employeeLevel = o.employeeLevel)
    }

    def asEmployeeEntity(companyId: Int, userId: Int): EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = companyId,
        userId = userId,
        positionId = o.position match { case Some(p) => p.id; case _ => None },
        shiftId = o.shift match { case Some(s) => s.id; case _ => None}, 
        departmentId = o.department match { case Some(d) => d.id; case _ => None},
        unionId = o.union match { case Some(u) => u.id; case _ => None},
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
        employeeLevel = emp.employeeLevel)
  }
}
