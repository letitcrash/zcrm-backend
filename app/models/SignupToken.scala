package models

import java.sql.Timestamp

case class SignupToken(
  token: String,
  email: String,
  createdAt: Timestamp,
  expiresAt: Timestamp,
  usedAt: Option[Timestamp]) {

  def isUsed = usedAt.nonEmpty

  def isExpired = expiresAt.before(new Timestamp(System.currentTimeMillis()))
}
