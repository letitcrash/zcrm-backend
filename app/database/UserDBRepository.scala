package database 

import java.math.BigInteger
import java.security.SecureRandom
import java.sql.Timestamp

import database.tables.SignupTokenEntity
import models.SignupToken
import play.api.Logger

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.util.{Success, Failure, Try}
import models.User 

object UserDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

}
