package controllers.session

import scala.util.{Failure, Success, Try}
import play.api.Logger

/**
 * Created by nicros on 07/05/14.
 */
/**
 *
 */
case class CRMRequestHeader(
  userId: Int,
  // The level the user has in the system, i.e admin or user
  // 1000 - 9999  = User
  // 100 - 999    = Administrators
  // 0-99         = Super
  userLevel: Int)

sealed trait CRMRequest[T] {
  def header: CRMRequestHeader
  def body: T
}

case class CRMSimpleRequest[T](
  header: CRMRequestHeader,
  body: T) extends CRMRequest[T]


case class CRMDBRequest[T](
  header: CRMRequestHeader,
  body: T) extends CRMRequest[T]
