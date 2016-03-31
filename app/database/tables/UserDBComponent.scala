package database.tables

import java.sql.Timestamp
import models.UserLevels
import slick.model.ForeignKeyAction.{Cascade, Restrict}


case class UserEntity(
  id: Option[Int] = None,
  username: String,
  userLevel: Int,
  profileId: Option[Int] = None,
  status: Char = 'I')

case class PasswordEntity(
  userId: Int,
  password: String,
  editedAt: Timestamp)


trait UserDBComponent extends DBComponent {
    this: DBComponent 
    with  ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val users = TableQuery[UserTable]
  val passwords = TableQuery[PasswordTable]
  
  class UserTable(tag: Tag) extends Table[UserEntity](tag, "tbl_user") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userLevel = column[Int]("user_level", O.Default(UserLevels.USER))
    def username = column[String]("username")
    def profileId = column[Int]("contact_profile_id")
    def status = column[Char]("status", O.Default('P'))
    def idx = index("username_uniq", username, unique = true)

    def contactProfile = foreignKey("fk_user_contact_profile", profileId, contactProfiles)(_.id, onUpdate = Restrict, onDelete = Cascade)

    override def * = (id.?, username, userLevel, profileId.?, status) <>
      (UserEntity.tupled, UserEntity.unapply)
  }

  class PasswordTable(tag: Tag) extends Table[PasswordEntity](tag, "tbl_passwords") {
    def userId = column[Int]("user_id")
    def password = column[String]("password")
    def editedAt = column[Timestamp]("edited_at")

    def fkUserId = foreignKey("fk_password_user_id", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade) 

    def * = (userId, password, editedAt) <> (PasswordEntity.tupled, PasswordEntity.unapply)

  }


}

