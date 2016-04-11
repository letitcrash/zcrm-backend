package security

import exceptions._
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.Headers
import authentikat.jwt._
import scala.util.{Success, Failure, Try}
import controllers.session._



object Security {
  import utils.JSFormat.employeeWithLevelFrmt
  private implicit val clearTokenFrmt  = Json.format[ClearToken]
  
  //TODO: should be refactored
  //val validateRequests = play.api.Configuration.getBoolean("validateRequests").getOrElse(false)
  val validateRequests = play.api.Play.current.configuration.getBoolean("validateRequests").getOrElse(false)

  def validateHeaders(headers: Headers): Try[CRMRequestHeader] = {
    if(!validateRequests) {
      try {
        val id = Integer.parseInt(headers.get(settings.USER_ID_HEADER).getOrElse("1"))
        Success(CRMRequestHeader(userId = id,
                                 userLevel = 9999,
                                 employeeAndLevel = EmployeeWithLevel(1, 23, 1)))
      } catch {
        case ex: Exception =>
          Logger.error("Failed to parse userId from headers", ex)
          Failure(ex)
      }
    } else {
      for {
        userId    <- getUserIdHeader(headers)
        clearToken  <- getJWTHeader(headers)
        rqHeader  <- createRequestHeader(userId, clearToken)
      } yield rqHeader
    }
  }
  
  def createSessionToken(user: User, employee: Employee): Try[String] = {
    if(user.id.isEmpty) {
      Logger.error("Tried to create session token with a User without id")
      return Failure(new Exception("Missing user id"))
    }
    try {
       encryptToken(ClearToken(uid = user.id.get,
                               ulvl = user.userLevel,
                               clvl = EmployeeWithLevel(employee.companyId,
                                                       employee.id.get,
                                                       employee.employeeLevel)))
    } catch {
      case ex: Exception =>
        Logger.error(f"Failed to create session token for user id: ${user.id}", ex)
        Failure(ex)
    }
  }

  def updateHeaderToken(header: CRMRequestHeader): Try[String] = {
    import System.currentTimeMillis
    val ts = currentTimeMillis()
    encryptToken(
      ClearToken(
        iss = Some(settings.APP_NAME),
        exp =  Some(ts + settings.TOKEN_EXPIRATION_TIME),
        iat = Some(ts),
        uid = header.userId,
        ulvl = header.userLevel,
        clvl = header.employeeAndLevel))
  }


 private[Security] def createRequestHeader(userId: Int, token: ClearToken): Try[CRMRequestHeader] = {
    if(userId != token.uid) {
      Failure(new NonMatchingUserIdException)
    } else if(token.iss.get != settings.APP_NAME) {
      Failure(new ThinIceException)
    } else if(settings.TOKEN_EXPIRATION_ENABLED && System.currentTimeMillis() > token.exp.get){
      Failure(new ExpiredTokenException)
    } else {
      Success(CRMRequestHeader(userId, token.ulvl, token.clvl))
    }
  }

  private[Security] def getJWTHeader(headers: Headers): Try[ClearToken] = {
    headers.get(settings.API_KEY_HEADER) match {
      case Some(token) => decryptToken(token)
      case _ => Failure(new MissingAccessTokenException)
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

  private[Security] def getUserIdHeader(headers: Headers): Try[Int] = {
    try {
      headers.get(settings.USER_ID_HEADER) match {
        case Some(id) => Success(Integer.parseInt(id))
        case _ => Failure(new MissingUserIdException)
      }
    } catch {
      case ex: Exception =>
        Logger.error("Failed to parse User id from headers", ex)
        Failure(new MissingUserIdException)
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
    ulvl: Int,
    clvl: EmployeeWithLevel 
  )

}
