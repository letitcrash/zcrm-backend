package database.tables

import java.sql.Timestamp
import models.UserLevels
import slick.model.ForeignKeyAction.{Cascade, Restrict}
import scala.util.{Success, Failure, Try}
import exceptions.UsernameAlreadyExistException
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

case class UserEntity(
  id: Option[Int] = None,
  username: String,
  userLevel: Int,
  profileId: Int,
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
    def username = column[String]("username", O.SqlType("VARCHAR(254)"))
    def profileId = column[Int]("contact_profile_id")    
    def status = column[Char]("status", O.Default('P'))

    def fkContactProfile= foreignKey("fk_user_contact_profile", profileId, contactProfiles)(_.id, onUpdate = Restrict, onDelete = Cascade)
    def idxUsername = index("username_uniq", username, unique = true)

    def * = (id.?, username, userLevel, profileId, status) <>
      (UserEntity.tupled, UserEntity.unapply)
  }

  class PasswordTable(tag: Tag) extends Table[PasswordEntity](tag, "tbl_passwords") {
    def userId = column[Int]("user_id")
    def password = column[String]("password")
    def editedAt = column[Timestamp]("edited_at")

    def fkUserId = foreignKey("fk_password_user_id", userId, users)(_.id, onUpdate = Restrict, onDelete = Cascade) 

    def * = (userId, password, editedAt) <> (PasswordEntity.tupled, PasswordEntity.unapply)

  }

  def insertUser(user: UserEntity): Future[UserEntity] = {
    db.run(users.filter(_.username === user.username).result.head)
      .map( userEntt => userEntt )
        .recoverWith{ case ex =>
           db.run(((users returning users.map(_.id) into ((user,id) => user.copy(id=Some(id)))) += user)
                    .map(userEntt => userEntt))
        }
  }

  def updateUser(user: UserEntity): Future[UserEntity] = {
      db.run(users.filter(_.id === user.id).update(user))
        .map( num => user)
  }
  
  def upsertUser(user: UserEntity): Future[UserEntity] = {
    if(user.id.isDefined) {
      updateUser(user)
    } else {
      insertUser(user)
    }
  }

}

