package models

import java.sql.Timestamp

case class Company(
  id: Option[Int] = None,
  name: String,
  contactProfile: Option[ContactProfile] = None,
  vatId: String,
  employeeTypes: Seq[String] = Seq(),
  lastModified: Option[Timestamp] = None)
