package models

import java.sql.Timestamp

case class ContactProfile(
  id:                 Option[Int] = None,
  firstname:          Option[String] = None,
  lastname:           Option[String] = None,
  email:              Option[String] = None,
  address:            Option[String] = None,
  city:               Option[String] = None,
  zipCode:            Option[String] = None,
  phoneNumberMobile:  Option[String] = None,
  phoneNumberHome:    Option[String] = None,
  phoneNumberWork:    Option[String] = None,
  lastModified:       Option[Timestamp] = None)