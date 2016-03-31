package utils.converters

import database.tables.SignupTokenEntity
import models.SignupToken

object SignupTokenConverter {

  implicit class EntityToSignupToken(e: SignupTokenEntity) {
    def asSignupToken =
      SignupToken(
        token = e.token,
        email = e.email,
        createdAt =  e.createdAt,
        expiresAt = e.expiresAt,
        usedAt =  e.usedAt)
  }

}
