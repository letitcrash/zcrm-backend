package exceptions
import java.sql.Timestamp

sealed class CRMException(val ecode: Int = -1, msg: String) extends Exception(msg)

/**
 * Security Exceptions
 *************************************************************************/
sealed class CRMSecurityException(msg: String, ecode: Int = -23) extends CRMException(ecode, msg)
case class InvalidTokenSignatureException(msg: String = "Invalid Token Signature") extends CRMSecurityException(msg)
case class InvalidAccessTokenException(msg: String = "Invalid Token") extends CRMSecurityException(msg)
case class MissingAccessTokenException(msg: String = "X-Access-Token header missing") extends CRMSecurityException(msg)
case class MissingUserIdException(msg: String = "X-User-Id header missing") extends CRMSecurityException(msg)
case class InvalidUserIdFormat(msg: String = "X-User-Id header must be an integer") extends CRMSecurityException(msg)
case class NonMatchingUserIdException(msg: String = "User id does not match token id") extends CRMSecurityException(msg)
case class ThinIceException(msg: String = "Thin thin") extends CRMSecurityException(msg)

