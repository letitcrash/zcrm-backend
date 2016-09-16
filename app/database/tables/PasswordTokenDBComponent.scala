package database.tables

import java.security.SecureRandom
import java.sql.Timestamp
import slick.profile.SqlProfile.ColumnOption.Nullable
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.math.BigInteger

case class PasswordTokenEntity(
  userId: Int,
  token: String,
  expires: Timestamp,
  addedAt: Timestamp,
  usedAt: Option[Timestamp] = None)

trait PasswordTokenDBComponent extends DBComponent{
  this: DBComponent =>

  import dbConfig.driver.api._

  val passwordTokens = TableQuery[PasswordTokenTable]

  class PasswordTokenTable(tag: Tag) extends Table[PasswordTokenEntity](tag, "tbl_password_tokens") {
    def userId = column[Int]("user_id")
    def token = column[String]("token", O.SqlType("VARCHAR(255)"))
    def expires = column[Timestamp]("expires_at")
    def addedAt = column[Timestamp]("added_at")
    def usedAt = column[Timestamp]("used_at", Nullable)

    def * = (userId, token, expires, addedAt, usedAt.?) <>(PasswordTokenEntity.tupled, PasswordTokenEntity.unapply)
      
  }

  private val random = SecureRandom.getInstance("NativePRNGNonBlocking")
  random.setSeed(random.generateSeed(55))


  //CRUD PasswordTokenDBComponent
  def insertPasswordToken(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
    db.run(passwordTokens += token).flatMap( num => getPasswordToken(token.userId))
  }


  def getPasswordToken(userId: Int): Future[PasswordTokenEntity] = {
    db.run(passwordTokens.filter(_.userId === userId).result.head)
  }

  def updatePasswordToken(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
    db.run(passwordTokens.filter(_.userId === token.userId).update(token))
                    .map( num => token)
  }

  //PswToken filters 
  def markPwdTokenAsUsed(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
      val updatedTkn = token.copy(usedAt = Some(new Timestamp(System.currentTimeMillis())))
      updatePasswordToken(updatedTkn)
  }

  def newPwdToken(userId: Int, validFor: Long = 86400000): Future[PasswordTokenEntity] = {
    val strToken = new BigInteger(220, random).toString(32)
    val currentTime = System.currentTimeMillis()
    val token = PasswordTokenEntity(userId = userId,
                                    token = strToken,
                                    expires = new Timestamp(currentTime + validFor),
                                    addedAt = new Timestamp(currentTime),
                                    usedAt = None)
    insertPasswordToken(token)
  }

  def validatePwdToken(userId: Int, strToken: String): Future[PasswordTokenEntity] = {
      db.run((for {
        token <- passwordTokens if token.userId === userId && token.token === strToken
      } yield token).sortBy(_.addedAt.desc).result.head)
  }

}

