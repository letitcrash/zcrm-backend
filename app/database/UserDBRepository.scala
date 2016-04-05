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


  def getUserByUsername(username: String): Future[User] = {
    import utils.converters.UserConverter._

     val interaction = for {
       user <- users if user.username === username
       profile <- contactProfiles if user.profileId === profile.id
     } yield (user, profile)

     db.run(interaction.result.head).map(_.asUser)

  }


}
