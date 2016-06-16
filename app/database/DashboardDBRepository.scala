package database


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Dashboard
import play.api.Logger


object DashboardDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._
  
  def getCountsForCompanyById(companyId: Int): Future[Dashboard] = {
    for{
        employeeCount <- getEmployeeCountByCompanyId(companyId)
        teamsCount    <- getTeamsCountByCompanyId(companyId)
        ticketsCount  <- getTicketsCountByCompanyId(companyId)
        projectsCount <- getProjectCountByCompanyId(companyId)
    }yield(Dashboard(employeeCount, teamsCount, ticketsCount, projectsCount))
  } 
}
