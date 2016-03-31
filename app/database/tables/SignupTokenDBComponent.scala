package database.tables

import java.sql.Timestamp
import slick.model.ForeignKeyAction.{Cascade, Restrict}

case class SignupTokenEntity(
  token: String,
  email: String,
  createdAt: Timestamp,
  expiresAt: Timestamp,
  usedAt: Option[Timestamp] = None)


trait SignupTokenDBComponent extends DBComponent {
  import dbConfig.driver.api._

  val signupTokens = TableQuery[SignupTokensTable]

  class SignupTokensTable(tag: Tag) extends Table[SignupTokenEntity](tag, "tbl_signup_tokens") {
    def token = column[String]("token", O.PrimaryKey)
    def email = column[String]("email" )
    def createdAt = column[Timestamp]("created_at")
    def expiresAt = column[Timestamp]("valid_for")
    def usedAt = column[Option[Timestamp]]("used_at", O.Default(None))

    override def *  = (token, email, createdAt, expiresAt, usedAt) <>
        (SignupTokenEntity.tupled, SignupTokenEntity.unapply)
  }
}
