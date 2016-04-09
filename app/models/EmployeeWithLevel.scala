package models

case class EmployeeWithLevel(
  employeeId: Int,
  companyId: Int,
  // The level the user has within a company, i.e admin or normal employee
  // 1000 - 9999  = user level
  // 100 - 999    = Human resource
  // 0-99         = Admin levels
  employeeLevel: Int = 9999)
