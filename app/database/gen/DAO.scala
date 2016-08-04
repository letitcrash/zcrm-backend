package database.gen

import javax.inject._
import play.api.Logger
import database.tables._
import scala.util.{Failure, Success, Try}

object current {
  val dao = new DAO()

  def initializeDatabase() = {
    // Logger.info("* Attempting to clear")
    // dao.clearDatabase
    // Logger.info("* Database cleared")
    dao.setupTables
    Logger.info("* Database ready")
  }

}

@Singleton
class DAO extends UserDBComponent
    with ContactProfileDBComponent
    with SignupTokenDBComponent
    with CompanyDBComponent
    with PositionDBComponent
    with EmployeeDBComponent
    with MailboxDBComponent
    with SavedExchangeMailDBComponent
    with ExchangeODSMailDBComponent
    with PasswordTokenDBComponent
    with TeamDBComponent
    with UnionDBComponent
    with ShiftDBComponent
    with DepartmentDBComponent
    with GroupDelegateDBComponent
    with DelegateDBComponent
    with TicketDBComponent
    with TicketActionDBComponent
    with TicketActionAttachedMailDBComponent
    with TicketActionAttachedFileDBComponent
    with TicketMemberDBComponent
    with TicketTeamMemberDBComponent
    with ProjectDBComponent
    with ProjectMembersDBComponent
    with ProjectTeamMemberDBComponent
    with FileFolderDBComponent
    with ClientDBComponent
    with ProjectClientDBComponent
    with TicketRequesterDBComponent
    with TicketClientDBComponent
    with FileDBComponent
    with PeriodDBComponent{
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
      //Logger.info("Dropping delegates         -> " + tryDrop(delegates.schema))
      //Logger.info("Dropping group delegates   -> " + tryDrop(groupDelegates.schema))
      Logger.info("Dropping employees         -> " + tryDrop(employees.schema))
      Logger.info("Dropping companies         -> " + tryDrop(companies.schema))
      Logger.info("Dropping passwordTokens    -> " + tryDrop(passwordTokens.schema))
      Logger.info("Dropping attached_mails    -> " + tryDrop(saved_mails.schema))
      Logger.info("Dropping mailboxes         -> " + tryDrop(mailboxes.schema))
      Logger.info("Dropping ods_mails         -> " + tryDrop(ods_mails.schema))
      Logger.info("Dropping saved_mails       -> " + tryDrop(saved_mails.schema))
      Logger.info("Dropping folders           -> " + tryDrop(folders.schema))
      Logger.info("Dropping files             -> " + tryDrop(files.schema))
      Logger.info("Dropping shifts            -> " + tryDrop(shifts.schema))
      Logger.info("Dropping teams             -> " + tryDrop(teams.schema))
      Logger.info("Dropping groups            -> " + tryDrop(teamGroups.schema))
      Logger.info("Dropping unions            -> " + tryDrop(unions.schema))
      Logger.info("Dropping departments       -> " + tryDrop(departments.schema))
      Logger.info("Dropping positions         -> " + tryDrop(positions.schema))
      Logger.info("Dropping tickets           -> " + tryDrop(tickets.schema))
      Logger.info("Dropping actions           -> " + tryDrop(actions.schema))
      Logger.info("Dropping attchedMails      -> " + tryDrop(attachedMails.schema))
      Logger.info("Dropping attchedFiless     -> " + tryDrop(attachedFiles.schema))
      Logger.info("Dropping ticket members    -> " + tryDrop(ticketMembers.schema))
      Logger.info("Dropping ticketTeam members-> " + tryDrop(ticketTeamMembers.schema))
      Logger.info("Dropping projects          -> " + tryDrop(projects.schema))
      Logger.info("Dropping project members   -> " + tryDrop(projectMembers.schema))
      Logger.info("Dropping projectTeams      -> " + tryDrop(projectTeamMembers.schema))
      Logger.info("Dropping clients           -> " + tryDrop(clients.schema))
      Logger.info("Dropping project clients   -> " + tryDrop(projectClients.schema))
      Logger.info("Dropping ticket clients    -> " + tryDrop(ticketClients.schema))
      Logger.info("Dropping ticket requesters -> " + tryDrop(ticketRequesters.schema))
      Logger.info("Dropping periods           -> " + tryDrop(periods.schema))
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
    Logger.info("Creating employees         -> " + tryCreate(employees.schema))
    Logger.info("Creating companies         -> " + tryCreate(companies.schema))
    Logger.info("Creating passwordTokens    -> " + tryCreate(passwordTokens.schema))
    Logger.info("Creating attached_mails    -> " + tryCreate(saved_mails.schema))
    //Logger.info("Creating delegates         -> " + tryCreate(delegates.schema))
    //Logger.info("Creating delegate groups   -> " + tryCreate(groupDelegates.schema))
    Logger.info("Creating mailboxes         -> " + tryCreate(mailboxes.schema))
    Logger.info("Creating ods_mails         -> " + tryCreate(ods_mails.schema))
    Logger.info("Creating saved_mails       -> " + tryCreate(saved_mails.schema))
    Logger.info("Creating folders           -> " + tryCreate(folders.schema))
    Logger.info("Creating files             -> " + tryCreate(files.schema))
    Logger.info("Creating teams             -> " + tryCreate(teams.schema))
    Logger.info("Creating team groups       -> " + tryCreate(teamGroups.schema))
    Logger.info("Creating shifts            -> " + tryCreate(shifts.schema))
    Logger.info("Creating unions            -> " + tryCreate(unions.schema))
    Logger.info("Creating departments       -> " + tryCreate(departments.schema))
    Logger.info("Creating positions         -> " + tryCreate(positions.schema))
    Logger.info("Creating actions           -> " + tryCreate(actions.schema))
    Logger.info("Creating attachedMails     -> " + tryCreate(attachedMails.schema))
    Logger.info("Creating attchedFiles      -> " + tryCreate(attachedFiles.schema))
    Logger.info("Creating tickets           -> " + tryCreate(tickets.schema))
    Logger.info("Creating tickets members   -> " + tryCreate(ticketMembers.schema))
    Logger.info("Creating ticketTeam members-> " + tryCreate(ticketTeamMembers.schema))
    Logger.info("Creating projects          -> " + tryCreate(projects.schema))
    Logger.info("Creating project members   -> " + tryCreate(projectMembers.schema))
    Logger.info("Creating projectTeams      -> " + tryCreate(projectTeamMembers.schema))
    Logger.info("Creating clients           -> " + tryCreate(clients.schema))
    Logger.info("Creating ticket clients    -> " + tryCreate(ticketClients.schema))
    Logger.info("Creating project clients   -> " + tryCreate(projectClients.schema))
    Logger.info("Creating ticket requesters -> " + tryCreate(ticketRequesters.schema))
    Logger.info("Creating periods           -> " + tryCreate(periods.schema))
    Success("Created All tables!")
  }

}

