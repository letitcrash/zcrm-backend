package database.gen

import javax.inject._
import play.api.Logger
import database.tables._
import scala.util.{Failure, Success, Try}

object current {
  val dao = new DAO()

  def initializeDatabase() {
    dao.setupTables
    Logger.info("* Database ready")
  }

}

@Singleton
class DAO extends UserDBComponent
   with ContactProfileDBComponent
   with SignupTokenDBComponent
   with CompanyDBComponent
   with EmployeeDBComponent
	 with PasswordTokenDBComponent{
 import dbConfig.driver.api._

 def setupTables() : Try[String] = {

  def tryCreate(ddlToCreate: dbConfig.driver.DDL ): Try[String] = {
   try {
        db.run(DBIO.seq(ddlToCreate.create))
        if(settings.LOG_DDL) {
          ddlToCreate.create.statements.foreach(println)
        }
        Success("")
      } catch {
        case ex: Exception =>
          Logger.error("Failed to create " + ddlToCreate.toString, ex)
          Failure(ex)
      }
    }

    Logger.info("Creating contactProfiles   -> " + tryCreate(contactProfiles.schema))
    Logger.info("Creating users             -> " + tryCreate(users.schema))
    Logger.info("Creating passwords         -> " + tryCreate(passwords.schema))
    Logger.info("Creating signupTokens      -> " + tryCreate(signupTokens.schema))
    Logger.info("Creating employees      -> " + tryCreate(employees.schema))
    Logger.info("Creating companies      -> " + tryCreate(companies.schema))
		Logger.info("Creating passworTokens  -> " + tryCreate(passwordTokens.schema))
    Success("Created All tables!")
  }

}

