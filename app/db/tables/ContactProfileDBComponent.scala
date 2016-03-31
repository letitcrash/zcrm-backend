package db.tables

import java.sql.Timestamp
//import javax.inject.Inject
//import play.api.db.slick.DatabaseConfigProvider
//import slick.driver.JdbcProfile
import models.UserLevels

import play.api.Play

case class ContactProfileEntity(
  id:                 Option[Int]    = None,
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

//class ContactProfileDBComponent @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {
trait ContactProfileDBComponent extends DBComponent {
  //val dbConfig = dbConfigProvider.get[JdbcProfile]
  //val db = dbConfig.db
  //val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  val contactProfiles = TableQuery[ContactProfileTable]

  class ContactProfileTable(tag: Tag) extends Table[ContactProfileEntity](tag, "tbl_contact") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstname = column[String]("firstname" )
    def lastname = column[String]("lastname")
    def email = column[String]("email")
    def address = column[String]("address")
    def city = column[String]("city")
    def zipCode = column[String]("zip_code")
    def phoneMobile = column[String]("phone_mobile")
    def phoneHome = column[String]("phone_home")
    def phoneWork = column[String]("phone_work")
    def lastModified = column[Timestamp]("last_modified", O.Default(new Timestamp(System.currentTimeMillis())))

    override def * = (id.?, firstname.?, lastname.?, email.?, address.?, city.?,
      zipCode.?, phoneMobile.?, phoneHome.?, phoneWork.?, lastModified.?) <>
        (ContactProfileEntity.tupled, ContactProfileEntity.unapply)
  }


}

