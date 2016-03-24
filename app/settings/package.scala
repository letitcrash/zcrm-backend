import play.api.Play.current

package object settings {
  val APP_NAME = current.configuration.getString("application.name").getOrElse("Undefined")
  val SECRET_KEY = current.configuration.getString("application.secret").getOrElse("secretkey")
  val SHOW_STACKTRACES = current.configuration.getBoolean("logging.showStacktraces").getOrElse(false)
  val SHOW_ERROR_MESSAGES = current.configuration.getBoolean("logging.showErrorMessages").getOrElse(false)
  val API_KEY_HEADER = current.configuration.getString("headers.access-token").getOrElse("X-Access-Token")
  val USER_ID_HEADER = current.configuration.getString("headers.user-id").getOrElse("X-User-Id")
  val TOKEN_ALGORITHM = current.configuration.getString("token.algorithm").getOrElse("HS256")
  val TOKEN_MOBILE_EXPIRATION_TIME: Long = current.configuration.getLong("token.mobileExpirationTime").getOrElse(86400000l) // Default to 1h
  val TOKEN_EXPIRATION_TIME: Long = current.configuration.getLong("token.expirationTime").getOrElse(1800000l) // Default to 30 mins
  val TOKEN_EXPIRATION_ENABLED: Boolean = current.configuration.getBoolean("token.expirationEnabled").getOrElse(true)
  val LOG_RESPONSE_TIMES: Boolean = current.configuration.getBoolean("logging.logResponseTimes").getOrElse(false)
  val DB_NETSOL_USERS: Boolean = current.configuration.getBoolean("data.netsol-users").getOrElse(false)
  val DB_FOGEN_USERS: Boolean = current.configuration.getBoolean("data.fogen-users").getOrElse(false)
  val DB_DUMMY_DATA: Boolean = current.configuration.getBoolean("data.dummy").getOrElse(false)
  val DB_FOGEN_MANAGEMENT: Boolean = current.configuration.getBoolean("data.fogen-management").getOrElse(false)
  val EXPORT_BASE_FOLDER: String = current.configuration.getString("export.path").getOrElse("export")
  val EXPORT_TIMELIMIT: Long = Integer.parseInt(current.configuration.getString("export.timelimit").getOrElse("10000"))
}
