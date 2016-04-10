package object settings {
  val APP_NAME = play.api.Configuration().getString("application.name").getOrElse("Undefined")
  val SECRET_KEY = play.api.Configuration().getString("application.secret").getOrElse("secretkey")
  val LOG_DDL = play.api.Configuration().getBoolean("logging.showDDL").getOrElse(false)
  val SHOW_STACKTRACES = play.api.Configuration().getBoolean("logging.showStacktraces").getOrElse(false)
  val SHOW_ERROR_MESSAGES = play.api.Configuration().getBoolean("logging.showErrorMessages").getOrElse(false)
  val API_KEY_HEADER = play.api.Configuration().getString("headers.access-token").getOrElse("X-Access-Token")
  val USER_ID_HEADER = play.api.Configuration().getString("headers.user-id").getOrElse("X-User-Id")
  val TOKEN_ALGORITHM = play.api.Configuration().getString("token.algorithm").getOrElse("HS256")
  val TOKEN_MOBILE_EXPIRATION_TIME: Long = play.api.Configuration().getLong("token.mobileExpirationTime").getOrElse(86400000l) // Default to 1h
  val TOKEN_EXPIRATION_TIME: Long = play.api.Configuration().getLong("token.expirationTime").getOrElse(1800000l) // Default to 30 mins
  val TOKEN_EXPIRATION_ENABLED: Boolean = play.api.Configuration().getBoolean("token.expirationEnabled").getOrElse(true)
  val LOG_RESPONSE_TIMES: Boolean = play.api.Configuration().getBoolean("logging.logResponseTimes").getOrElse(false)
  val DB_NETSOL_USERS: Boolean = play.api.Configuration().getBoolean("data.netsol-users").getOrElse(false)
  val DB_FOGEN_USERS: Boolean = play.api.Configuration().getBoolean("data.fogen-users").getOrElse(false)
  val DB_DUMMY_DATA: Boolean = play.api.Configuration().getBoolean("data.dummy").getOrElse(false)
  val DB_FOGEN_MANAGEMENT: Boolean = play.api.Configuration().getBoolean("data.fogen-management").getOrElse(false)
  val EXPORT_BASE_FOLDER: String = play.api.Configuration().getString("export.path").getOrElse("export")
  val EXPORT_TIMELIMIT: Long = Integer.parseInt(play.api.Configuration().getString("export.timelimit").getOrElse("10000"))
}
