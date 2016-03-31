package models

object UserLevels {
  // The level the user has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  val SUPER = 99
  val ADMIN = 999
  val USER = 9999
}
