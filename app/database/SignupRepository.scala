package database 

import java.math.BigInteger
import java.security.SecureRandom
import java.sql.Timestamp

import database.tables.SignupTokenEntity
import models.SignupToken
import play.api.Logger

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

import scala.util.{Success, Failure, Try}

object SignupRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  private[SignupRepository] val random = new SecureRandom()
  random.setSeed(random.generateSeed(55))

  def createTokenForEmail(email: String, validFor: Long = 7200000): Try[SignupToken] = {
    import utils.converters.SignupTokenConverter.EntityToSignupToken
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

  def findToken(token: String): Future[SignupToken] = {
    import utils.converters.SignupTokenConverter.EntityToSignupToken
    db.run(signupTokens.filter(_.token === token).result.head).map(_.asSignupToken)
  }

  def findUsableToken(email: String): Future[SignupToken] = {
    import utils.converters.SignupTokenConverter.EntityToSignupToken
    val now = new Timestamp(System.currentTimeMillis())
     db.run(signupTokens
          .filter(e => e.email === email && e.usedAt.isEmpty && e.expiresAt < now)
          .sortBy(_.createdAt.desc).result.head)
          .map(_.asSignupToken)
  }

  def markTokenUsed(token: String): Future[Int] = {
        db.run(signupTokens
          .filter(_.token === token)
          .map(_.usedAt)
          .update(Some(new Timestamp(System.currentTimeMillis()))))

  }


}
