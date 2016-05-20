package models

import java.sql.Timestamp

case class Company(
  id: Option[Int] = None,
  name: String,
  contactProfile: Option[ContactProfile] = None,
  vatId: String,
  delegates: Option[List[Delegate]] = None,
  shifts: Option[List[Shift]] = None,
  departmets: Option[List[Department]] = None, 
  unions: Option[List[Union]] = None, 
  teams: Option[List[Team]] = None, 
  positions: Option[List[Position]] = None, 
  lastModified: Option[Timestamp] = None)
