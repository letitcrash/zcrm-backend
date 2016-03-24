package security

import exceptions._
import models._
import play.api.Play.current
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.Headers
import authentikat.jwt._
import scala.util.{Success, Failure, Try}

object Security {
    
  private implicit val clearTokenFrmt  = Json.format[ClearToken]
  
  def createSessionToken(user: User): Try[String] = {
    if(user.id.isEmpty) {
      Logger.error("Tried to create session token with a User without id")
      return Failure(new Exception("Missing user id"))
    }
    try {
       encryptToken(ClearToken(uid = user.id.get, ulvl = user.userLevel))
    } catch {
      case ex: Exception =>
        Logger.error(f"Failed to create session token for user id: ${user.id}", ex)
        Failure(ex)
    }
  }

  private[Security] def decryptToken(token: String): Try[ClearToken] = {
    try {
      if(!JsonWebToken.validate(token, settings.SECRET_KEY)){
       return Failure(new InvalidTokenSignatureException)
      }
      val claims: Option[String] = token match {
        case JsonWebToken(header, claimsSet, signature) =>
          Some(claimsSet.asJsonString)
        case x =>
          None
      }
      Json.parse(claims.get).validate[ClearToken].map { token =>
        Success(token)
      } recoverTotal (e => Failure(new InvalidAccessTokenException))
    } catch {
      case ex: Exception =>
        Logger.error("Failed to decrypt token: " + token, ex)
        Failure(new InvalidAccessTokenException)
    }
  }


  private[Security] def encryptToken(key: ClearToken): Try[String] = {
    import System.currentTimeMillis
    val ts = currentTimeMillis()
    try {
      val header = JwtHeader(settings.TOKEN_ALGORITHM)
      val enrichedToken = key.copy(iss = Some(settings.APP_NAME),
                                   iat = Some(ts),
                                   exp = Some(ts + settings.TOKEN_EXPIRATION_TIME))
      val claimsSet = JwtClaimsSet(Json.stringify(Json.toJson(enrichedToken)))
      val jwt = JsonWebToken(header, claimsSet, settings.SECRET_KEY)
      Success(jwt)
    } catch {
      case ex: Exception =>
        Logger.error("Failed to encrypt clearToken: " + key.toString)
        Failure(ex)
    }
  }

  private case class ClearToken(
    iss: Option[String] = None,
    exp: Option[Long] = None,
    iat: Option[Long] = None, 
    uid: Int, 
    ulvl: Int 
  )

}