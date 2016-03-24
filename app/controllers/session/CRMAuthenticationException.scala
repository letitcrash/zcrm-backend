package controllers.session

/**
 * Created by nicros on 2014-06-24.
 */


sealed abstract class CRMAuthenticationException(msg: String) extends Exception(msg)

class InsufficientRightsException(msg: String = "Insufficient rights to perform this operation")
  extends CRMAuthenticationException(msg)

class InvalidUserException(msg: String = "You are not allowed to perform this action")
  extends CRMAuthenticationException(msg)
