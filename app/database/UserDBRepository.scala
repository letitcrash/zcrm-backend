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

  def getUserByUsername(username: String): Future[User] = {
    import utils.converters.UserConverter._

     val interaction = for {
       user <- users if user.username === username
       profile <- contactProfiles if user.profileId === profile.id
     } yield (user, profile)

     db.run(interaction.result.head).map(_.asUser)

  }


}
