package utils.converters

import database.tables.{CompanyEntity, ContactProfileEntity, EmployeeEntity, UserEntity}
import models.{Employee, User}

object EmployeeConverter {


  implicit class EntityUserWithProfile
  (tup: (EmployeeEntity, (UserEntity, ContactProfileEntity))) {

    def toEmployee(): Employee = {
      import UserConverter.EntityToUser
      Employee(
        id = tup._1.id,
        user = Option(tup._2.asUser),
        employmentNumberType = tup._1.employmentNumberType,
        employmentNumberValue = tup._1.employmentNumberValue,
        isSubContractor = tup._1.isSubContractor,
        subContractorCompanyName = tup._1.subContractorCompanyName,
        subContractorOrgNr = tup._1.subContractorOrgNr,
        companyId = tup._1.companyId,
        employeeType = tup._1.employeeType,
        employeeLevel = tup._1.employeeLevel,
        comment = tup._1.comment)
    }
  }

  implicit class Model(o: Employee) {
    def asEmployeeEntity(companyId: Int, userId: Int): EmployeeEntity = {
      EmployeeEntity(
        id = o.id,
        companyId = companyId,
        userId = Some(userId),
        employeeType = o.employeeType,
        employmentNumberType = o.employmentNumberType,
        employmentNumberValue = o.employmentNumberValue,
        isSubContractor = o.isSubContractor,
        subContractorOrgNr = o.subContractorOrgNr,
        subContractorCompanyName = o.subContractorCompanyName,
        comment = o.comment,
        employeeLevel = o.employeeLevel)
    }
  }

  implicit class Entity
  (o: (EmployeeEntity, UserEntity, ContactProfileEntity)) {

    def asEmployee() = {
      import UserConverter.EntityToUser

      val emp = o._1
      Employee(
        id = emp.id,
        user = Some((o._2, o._3).asUser),
        employmentNumberType = emp.employmentNumberType,
        employmentNumberValue = emp.employmentNumberValue,
        isSubContractor = emp.isSubContractor,
        subContractorCompanyName = emp.subContractorCompanyName,
        subContractorOrgNr = emp.subContractorOrgNr,
        comment = emp.comment,
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
        employmentNumberType = emp.employmentNumberType,
        employmentNumberValue = emp.employmentNumberValue,
        isSubContractor = emp.isSubContractor,
        subContractorCompanyName = emp.subContractorCompanyName,
        subContractorOrgNr = emp.subContractorOrgNr,
        comment = emp.comment,
        companyId = comp.id.getOrElse(0),
        employeeType = emp.employeeType,
        employeeLevel = emp.employeeLevel)
  }
}
