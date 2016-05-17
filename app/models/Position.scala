package models

import java.sql.Timestamp

case class Position(
  id: Option[Int] = None,
  companyId: Int,
  name: String)
