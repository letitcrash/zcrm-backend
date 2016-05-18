package models

case class Employee(
  id: Option[Int] = None,
  user: Option[User] = None,
  companyId: Int,
  positionId: Option[Int] = None, 
  unionId: Option[Int] = None, 
  //employeeType: Option[String] = None,
  employeeLevel: Int = UserLevels.USER)
