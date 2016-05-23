package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Company,PagedResult}

object CompanyDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def saveCompany(company: Company): Future[Company] = {
    import utils.converters.CompanyConverter.{CompanyToEntity, EntitiesToCompany}
    import utils.converters.ContactProfileConverter.ContactProfileToEntity
    import database.tables.ContactProfileEntity

    //TODO: should be transactionally
    (for {
        profile <- upsertProfile(company.contactProfile.fold(ContactProfileEntity())(_.asEntity()))
        company <- upsertCompany(company.asEntity(profile.id.get))
    } yield (company, profile)).map(_.asCompany)
  }


  def getAggregatedCompany(id: Int): Future[Company] = {
    import utils.converters.CompanyConverter.EntitiesToCompany
    for {
      companyEntt <- getCompanyWithProfileById(id)
      delegatesEntt <- getDelegateEntitiesByCompanyId(id)
      shiftsEntt <-  getShiftEntitiesByCompanyId(id)
      departmetsEntt <- getDepartmentEntitiesByCompanyId(id)
      unionsEntt <- getUnionEntitiesByCompanyId(id)
      teamsEntt <-  getTeamEntitiesByCompanyId(id)
      positionsEntt <- getPositionEntitiesByCompanyId(id)
    } yield  companyEntt.asAggregatedCompany(delegatesEntt, shiftsEntt, departmetsEntt, unionsEntt, teamsEntt, positionsEntt)
  }

  def getCompany(id: Int): Future[Company] = {
    import utils.converters.CompanyConverter.EntitiesToCompany
    getCompanyWithProfileById(id).map(_.asCompany)
  }

  def getAllCompanies :Future[List[Company]] = {
    import utils.converters.CompanyConverter._
    getCompanyEntities.map(list => list.map(_.asCompany))
  }

  def searchCompanyByName(pageSize: Int, pageNr: Int, searchTerm: Option[String]): Future[PagedResult[Company]] = {
    import utils.converters.CompanyConverter.EntitiesToCompany
    searchCompanyEntitiesByName(pageSize, pageNr, searchTerm).map{dbPage =>
        PagedResult[Company](pageSize = dbPage.pageSize,
                             pageNr = dbPage.pageNr,
                             totalCount = dbPage.totalCount,
                             data = dbPage.data.map(_.asCompany))}
  }

}
