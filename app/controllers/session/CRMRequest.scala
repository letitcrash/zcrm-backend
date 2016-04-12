package controllers.session

import scala.util.{Failure, Success, Try}
import play.api.Logger
import models._

case class CRMRequestHeader(
  userId: Int,
  // The level the USER has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  // The level the EMPLOYEE has in the system, i.e admin or user
  // 1000 - 9999  = Employee
  // 100 - 999    = Manager
  // 0-99         = Owner

  userLevel: Int,
  employeeAndLevel: EmployeeWithLevel){

  def isAdmin = userLevel <= UserLevels.ADMIN

  def belongsToCompany(companyId: Int) = 
    employeeAndLevel.companyId == companyId 

  def isCompanyManager =
    employeeAndLevel.employeeLevel <= EmployeeLevels.MANAGER && 
    employeeAndLevel.employeeLevel > EmployeeLevels.OWNER 

  def isCompanyOwner = 
    employeeAndLevel.employeeLevel <= EmployeeLevels.OWNER

  }
    

sealed trait CRMRequest[T] {
  def header: CRMRequestHeader
  def body: T
}

case class CRMSimpleRequest[T](
  header: CRMRequestHeader,
  body: T) extends CRMRequest[T]


case class CRMDBRequest[T](
  header: CRMRequestHeader,
  body: T) extends CRMRequest[T]
