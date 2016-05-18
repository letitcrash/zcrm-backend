package database.gen

import javax.inject._
import play.api.Logger
import database.tables._
import scala.util.{Failure, Success, Try}

object current {
  val dao = new DAO()

  def initializeDatabase() {
   // Logger.info("* Attempting to clear")
   // dao.clearDatabase
   // Logger.info("* Database cleared")
    dao.setupTables
    Logger.info("* Database ready")
  }

}

@Singleton
class DAO extends UserDBComponent
   with TaskAttachedMailDBComponent
   with TaskDBComponent
   with ContactProfileDBComponent
   with SignupTokenDBComponent
   with CompanyDBComponent
   with PositionDBComponent
   with EmployeeDBComponent
   with MailboxDBComponent
   with PasswordTokenDBComponent
   with TeamDBComponent
   with UnionDBComponent
   with ShiftDBComponent
   with FileDBComponent{
 import dbConfig.driver.api._

  def clearDatabase: Try[String] = {

    def tryDrop(ddlToDrop: dbConfig.driver.DDL): Try[String] = {
      try {
         db.run(DBIO.seq(ddlToDrop.drop))
        if(settings.LOG_DDL) {
          ddlToDrop.drop.statements.foreach(println)
        }
        Success("")
      } catch {
        case ex: Exception =>
          Logger.error("Failed to drop ddl " + ddlToDrop.toString)
          Failure(ex)
      }
    }

    try {
      Logger.info("Dropping contactProfiles   -> " + tryDrop(contactProfiles.schema))
      Logger.info("Dropping users             -> " + tryDrop(users.schema))
      Logger.info("Dropping passwords         -> " + tryDrop(passwords.schema))
      Logger.info("Dropping signupTokens      -> " + tryDrop(signupTokens.schema))
      Logger.info("Dropping employees      -> "    + tryDrop(employees.schema))
      Logger.info("Dropping companies      -> "    + tryDrop(companies.schema))
      Logger.info("Dropping passworTokens  -> "    + tryDrop(passwordTokens.schema))
      Logger.info("Dropping tasks  -> "            + tryDrop(tasks.schema))
      Logger.info("Dropping taskAttachedMails  -> "    + tryDrop(taskAttachedMails.schema))
      Logger.info("Dropping mailboxes  -> "    + tryDrop(mailboxes.schema))
      Logger.info("Dropping files  -> "    + tryDrop(files.schema))
      Logger.info("Dropping shifts  -> "    + tryDrop(shifts.schema))
      Logger.info("Dropping teams  -> "    + tryDrop(teams.schema))
      Logger.info("Dropping unions  -> "    + tryDrop(unions.schema))
      Logger.info("Dropping teams  -> "    + tryDrop(teamGroups.schema))
      Logger.info("Dropping positions -> " + tryDrop(positions.schema))
      Success("Tables dropped")
    } catch {
      case ex: Exception =>
        Failure(ex)
    }
  }
 

 def setupTables(): Try[String] = {

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
    Logger.info("Creating tasks -> "          + tryCreate(tasks.schema))
    Logger.info("Creating taskAttachedMails -> " + tryCreate(taskAttachedMails.schema))
    Logger.info("Creating mailboxes -> " + tryCreate(mailboxes.schema))
    Logger.info("Creating files  -> "    + tryCreate(files.schema))
    Logger.info("Creating teams -> "    + tryCreate(teams.schema))
    Logger.info("Creating shifts -> "    + tryCreate(shifts.schema))
    Logger.info("Creating unions -> "    + tryCreate(unions.schema))
    Logger.info("Creating teams -> "    + tryCreate(teamGroups.schema))
    Logger.info("Creating positions -> " + tryCreate(positions.schema))
    Success("Created All tables!")
  }

}

