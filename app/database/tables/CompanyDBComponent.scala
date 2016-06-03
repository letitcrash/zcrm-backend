package database.tables

import java.sql.Timestamp
import System.currentTimeMillis
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.PagedDBResult


case class CompanyEntity(id: Option[Int] = None,
                         name: String,
                         contactProfileId: Int,
                         vatId: String,
                         lastModified: Option[Timestamp] = Some(new Timestamp(System.currentTimeMillis())))

trait CompanyDBComponent extends DBComponent
  with ContactProfileDBComponent {
  this: DBComponent =>

  import dbConfig.driver.api._

  val companies = TableQuery[CompanyTable]

  class CompanyTable(tag: Tag) extends Table[CompanyEntity](tag, "tbl_company") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def profileId = column[Int]("contact_profile_id")
    def vatId = column[String]("vat_id", O.SqlType("VARCHAR(255)"))
    def lastModified = column[Timestamp]("last_modified")

    def contactProfile = foreignKey("fk_company_contact_profile", profileId, contactProfiles)(_.id)

    override def * = (id.?, name, profileId, vatId, lastModified.?) <> (CompanyEntity.tupled, CompanyEntity.unapply)
  }

  def companyWithProfile = companies join contactProfiles on (_.profileId === _.id)

  //CRUD CompanyEntity
  def insertCompany(company: CompanyEntity): Future[CompanyEntity] = {
      val ts = Some(new Timestamp(System.currentTimeMillis()))
      val row = company.copy(lastModified = ts)
        db.run((companies returning companies.map(_.id) 
          into ((company,id) => company.copy(id=Some(id)))) += row)
  }

  def getCompanyWithProfileById(id: Int): Future[(CompanyEntity, ContactProfileEntity)] = {
    db.run(companyWithProfile.filter(_._1.id === id).result.head)
  }

  def updateCompany(company: CompanyEntity):Future[CompanyEntity] = {
    val newCompany = company.copy(lastModified = Some(new Timestamp(System.currentTimeMillis())))
       db.run(companies.filter(_.id === company.id).update(newCompany))
                     .map(num => newCompany)
  }


  //CompanyEntity filters
  def upsertCompany(company: CompanyEntity): Future[CompanyEntity] = {
    if(company.id.isDefined) {updateCompany(company)}
    else {insertCompany(company)}
  }


  def getCompanyEntities: Future[List[(CompanyEntity, ContactProfileEntity)]] = {
    db.run(companyWithProfile.result).map(_.toList)
  }
   
  def searchCompanyEntitiesByName(pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[(CompanyEntity, ContactProfileEntity)]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        companyWithProfile.filter{ tup => tup._1.name.like(s)}
      }.getOrElse(companyWithProfile)  

    val pageRes = baseQry
      .sortBy(_._1.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( compList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = compList)
          )
        )
  }
}

