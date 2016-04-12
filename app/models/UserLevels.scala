package models

object UserLevels {
  // The level the USER has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  val SUPER = 99
  val ADMIN = 999
  val USER = 9999
}

object EmployeeLevels{
  // The level the EMPLOYEE has in the system, i.e admin or user
  // 1000 - 9999  = Employee
  // 100 - 999    = Manager
  // 0-99         = Owner
  val OWNER = 99
  val MANAGER = 999
  val EMPLOYEE = 9999

}
