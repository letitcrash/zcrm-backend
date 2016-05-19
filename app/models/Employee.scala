package models

case class Employee(
  id: Option[Int] = None,
  user: Option[User] = None,
  companyId: Int,
  position: Option[Position] = None, 
  shift: Option[Shift] = None,
  department: Option[Shift] = None, 
  union: Option[Union] = None, 
  teams: Option[List[Team]] = None,
  delegates: Option[List[Delegate]] = None, 
  employeeLevel: Int = UserLevels.USER)
