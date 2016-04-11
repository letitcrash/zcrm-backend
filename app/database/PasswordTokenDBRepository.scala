package database

import scala.concurrent.Future
import database.tables.PasswordTokenEntity
import java.sql.Timestamp
import java.math.BigInteger
import java.security.SecureRandom

//TODO: delete it 
object PasswordTokenDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._
  /*

  private val random = new SecureRandom()
  random.setSeed(random.generateSeed(55))

	def markTokenAsUsed(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
      val updatedTkn = token.copy(usedAt = Some(new Timestamp(System.currentTimeMillis())))
			updatePasswordToken(updatedTkn)
  }

  def newToken(userId: Int, validFor: Long = 86400000): Future[PasswordTokenEntity] = {
    val strToken = new BigInteger(220, random).toString(32)
    val currentTime = System.currentTimeMillis()
    val token = PasswordTokenEntity(userId = userId,
																		token = strToken,
																		expires = new Timestamp(currentTime + validFor),
																		addedAt = new Timestamp(currentTime),
																		usedAt = None)
		insertPasswordToken(token)
	}

  def validateToken(userId: Int, strToken: String): Future[PasswordTokenEntity] = {
			db.run((for {
        token <- passwordTokens if token.userId === userId && token.token === strToken
      } yield token).sortBy(_.addedAt.desc).result.head)
  }
  */
		
}
