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

/**
 * Password token exception
 *************************************************************************/
class ExpiredTokenException extends Exception("Expired Token")
class InvalidTokenException extends Exception("Invalid Token")
class TokenAlreadyUsedException(val usedAt: Timestamp) extends Exception("Token Already used")


/**
 * Password errors
 */
class InvalidPasswordException extends Exception("Invalid password")


/**
 * Common database exceptions
 */
class RecordAlreadyExistDBException extends CRMException ( -4, "Record already exists")
class EmptyResultDBException extends CRMException( -3, "No result for DB query ")
class UsernameAlreadyExistException extends CRMException(-2, "Username already exists")
class IdDoesNotExist(id: Int) extends CRMException(-1, f"Object with id $id does not exist")


/**
 * Common errors
 */
class LogicalException(msg: String) extends CRMException(-23, f"LOGICAL ERROR: $msg")

/**
 * Invalid file format
 */
class InvalidFileFormatException(msg: String) extends Exception("Invalid file format")

/**
 * Invalid blip order
 */
class InvalidBlipOrderException(msg: String) extends Exception("Invalid blip order")

class PeriodNotStoppedException extends Exception("Period was not stopped, cannot calculate duration.")
