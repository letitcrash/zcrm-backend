package database 

import java.math.BigInteger
import java.security.SecureRandom
import java.sql.Timestamp

import database.tables.SignupTokenEntity
import models.SignupToken
import play.api.Logger

import scala.util.{Success, Failure, Try}

object SignupRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  //Random generator
  private[SignupRepository] val random = new SecureRandom()
  random.setSeed(random.generateSeed(55))

  //Creates a new token for given user.
  def createTokenForEmail(email: String, validFor: Long = 7200000): Try[SignupToken] = {
    import utils.converters.SignupTokenConverter.EntityToSignupToken

    Logger.info("createTokenForEmail is running ... ") 

    val now = System.currentTimeMillis()
    val tokenEntity = SignupTokenEntity(
      token = new BigInteger(220, random).toString(32),
      email = email,
      createdAt = new Timestamp(now),
      expiresAt = new Timestamp(now + validFor),
      usedAt = None)
    try {
      db.run( DBIO.seq( signupTokens += tokenEntity) )
      Success(tokenEntity.asSignupToken)
    } catch {
      case ex: Exception =>
        Failure(ex)
    }
  }


}
