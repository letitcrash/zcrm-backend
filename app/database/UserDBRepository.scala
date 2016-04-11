package database 

import java.math.BigInteger
import java.security.SecureRandom
import java.sql.Timestamp

import database.tables.SignupTokenEntity
import models.SignupToken
import play.api.Logger

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.util.{Success, Failure, Try}
import models.User 

object UserDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def saveUser(user: User): Future[User] = {
    import utils.converters.UserConverter.{UserToEntity, EntityToUser}
    import utils.converters.ContactProfileConverter.ContactProfileToEntity
    import database.tables.ContactProfileEntity

    //TODO: should be transactionally
    (for {
        profile <- upsertProfile(user.contactProfile.fold(ContactProfileEntity()) (_.asEntity()))
        user    <- upsertUser(user.asEntity(profile.id.get))
    } yield (user, profile)).map(_.asUser)

  }

  //TODO: should be refactored 
  def getUserByUsername(username: String): Future[User] = {
    import utils.converters.UserConverter._

     val interaction = for {
       userEntt <- users if userEntt.username === username
       profileEntt <- contactProfiles if userEntt.profileId === profileEntt.id
     } yield (userEntt, profileEntt)

     db.run(interaction.result.head).map(_.asUser)

  }

  def setPasswordForUser(userId: Int, password: String): Future[User] = {
    import utils.converters.UserConverter.EntityToUser
    import database.tables.ContactProfileEntity
    for {
      userEntt <- setUserPassword(userId, password)
      profileEntt <- getProfileById(userEntt.profileId)
    } yield (userEntt, profileEntt).asUser
  }

  def loginUser(username: String, rawPassword: String): Future[User] = {
    import utils.converters.UserConverter.{EntityToUser}
    checkUserStatusByUserName(username).flatMap(isUserActive =>
      if(isUserActive){
          checkPassword(username, rawPassword).flatMap( isPwdCorrect =>
            if(isPwdCorrect){
              (for {
                user    <- getUserByUserUsername(username)
                profile <- getProfileById(user.profileId)
              } yield (user, profile)).map(_.asUser)
            }else { throw new Exception("Incorrect password") })
        }else{ throw new Exception("User inactive!") }
    )
  }


 def createPasswordToken(user: User): Future[String] = {
   newPwdToken(user.id.get).map( pwdToken => pwdToken.token)
 }
	
 

}
