package models

case class Client(
  id: Option[Int] = None,
  companyId: Int,
  contactProfile: ContactProfile)
