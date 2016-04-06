package database.tables

import java.sql.Timestamp
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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

trait ContactProfileDBComponent extends DBComponent {
  this: DBComponent =>

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

  //CRUD ContactProfileEntity
  def insertProfile(profile: ContactProfileEntity): Future[ContactProfileEntity] = {
      val ts = Some(new Timestamp(System.currentTimeMillis()))
      db.run(((contactProfiles returning contactProfiles.map(_.id)
         into ((profile,id) => profile.copy(id=Some(id)))) 
            += profile.copy(lastModified = ts))
              .map(profileEntt => profileEntt))
  }

  def getProfileById(id: Int): Future[ContactProfileEntity] = {
    db.run(contactProfiles.filter(_.id === id).result.head)
  }
  
  def updateProfile(profile: ContactProfileEntity): Future[ContactProfileEntity] = {
      val newProfile = profile.copy(lastModified = Some(new Timestamp(System.currentTimeMillis())))
       db.run(contactProfiles.filter(_.id === profile.id).update(newProfile))
        .map( num => newProfile)
  }


  //Profile Filters 
  def upsertProfile(profile: ContactProfileEntity): Future[ContactProfileEntity] = {
    if(profile.id.isDefined) {
      updateProfile(profile)
    } else {
      insertProfile(profile)
    }
  }

}

