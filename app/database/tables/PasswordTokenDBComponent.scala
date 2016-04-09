package database.tables

import java.security.SecureRandom
import java.sql.Timestamp

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
    def token = column[String]("token")
    def expires = column[Timestamp]("expires_at")
    def addedAt = column[Timestamp]("added_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def usedAt = column[Timestamp]("user_at")

    def * = (userId, token, expires, addedAt, usedAt.?) <>(PasswordTokenEntity.tupled, PasswordTokenEntity.unapply)
			
  }

	//CRUD PasswordTokenDBComponent
	def insertPasswordToken(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
		db.run((passwordTokens returning passwordTokens.map(_.userId)
										into ((token,id) => token.copy(userId=id))) += token)
	}

	/*
	def getPasswordTokenByUserId(userId: Int): Future[PasswordTokenEntity] = {
		db.run(passwordTokens.filter(_.userId === userId)
	}*/

	def updatePasswordToken(token: PasswordTokenEntity): Future[PasswordTokenEntity] = {
		db.run(passwordTokens.filter(_.userId === token.userId).update(token))
                    .map( num => token)
	}
}

