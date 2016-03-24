package models

case class User(
  id: Option[Int] = None,
  username: String,

  // The level the user has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  userLevel: Int = 9999,
  contactProfile: Option[ContactProfile] = None)