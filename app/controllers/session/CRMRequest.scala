package controllers.session

import scala.util.{Failure, Success, Try}
import play.api.Logger
import models._

case class CRMRequestHeader(
  userId: Int,
  // The level the user has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  userLevel: Int,
  employeeAndLevel: EmployeeWithLevel){

  def isAdmin = userLevel <= UserLevels.ADMIN

  def isCompanyOwnerOrManagerOrAdmin(companyId: Int) = 
    isAdmin ||
    employeeAndLevel.companyId == companyId &&
    employeeAndLevel.employeeLevel <= EmployeeLevels.MANAGER
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
