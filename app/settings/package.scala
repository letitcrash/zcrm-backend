import play.api.Play.current

package object settings {
    
     val APP_NAME =  current.configuration.getString("application.name").getOrElse("Undefined")
     val SECRET_KEY =  current.configuration.getString("play.crypto.secret").getOrElse("secretkey")
     val TOKEN_ALGORITHM = current.configuration.getString("token.algorithm").getOrElse("HS256")
     val TOKEN_EXPIRATION_TIME: Long = current.configuration.getLong("token.expirationTime").getOrElse(1800000l) // Default to 30 mins
     
}