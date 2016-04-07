package models

case class Employee(
  id: Option[Int] = None,
  user: Option[User] = None,
  companyId: Int,
  employeeType: Option[String] = None,
  employeeLevel: Int = UserLevels.USER)
