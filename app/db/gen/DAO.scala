package dbb.gen

import javax.inject._
import play.api.Logger
import db.tables._
import scala.util.{Failure, Success, Try}

object current {
  val dao = new DAO()

  def initializeDatabase() {
    dao.setupTables
    Logger.info("* Database ready")
  }

}


class DAO extends UserDBComponent
   with ContactProfileDBComponent {
 import dbConfig.driver.api._

 def setupTables() : Try[String] = {

  def tryCreate(ddlToCreate: dbConfig.driver.DDL ): Try[String] = {
   try {
        db.run(DBIO.seq(ddlToCreate.create))
        Success("")
      } catch {
        case ex: Exception =>
          Logger.error("Failed to create " + ddlToCreate.toString, ex)
          Failure(ex)
      }
    }

    Logger.info("Creating contactProfiles   -> " + tryCreate(contactProfiles.schema))
    Logger.info("Creating users             -> " + tryCreate(users.schema))

    Success("Created All tables!")
  }

}

