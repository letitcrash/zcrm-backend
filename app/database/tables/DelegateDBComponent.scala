package database.tables

import java.sql.Timestamp
import scala.concurrent.Future
import slick.profile.SqlProfile.ColumnOption.Nullable

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import database.PagedDBResult

case class DelegateEntity(
  id: Option[Int],
  companyId: Int,
  name: String,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait DelegateDBComponent extends DBComponent
  with CompanyDBComponent {
  this: DBComponent =>

  import dbConfig.driver.api._

  val delegates = TableQuery[DelegateTable]

  class DelegateTable(tag: Tag) extends Table[DelegateEntity](tag, "tbl_delegate") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def createdAt = column[Timestamp]("created_at", Nullable)
    def updatedAt = column[Timestamp]("updated_at", Nullable)

    def fkCompany = foreignKey("fk_delegate_company", companyId, companies)(_.id)

    override def * = (id.?, companyId, name, createdAt, updatedAt) <> (DelegateEntity.tupled, DelegateEntity.unapply)
  }

  def delegateQry(companyId: Int) = {
    delegates.filter(_.companyId === companyId) 
  }

  //CRUD
  def insertDelegate(delegate: DelegateEntity): Future[DelegateEntity] = {
    db.run(
      (delegates returning delegates.map(_.id) into ((delegate, id) => delegate.copy(id = Some(id))))
        += delegate)
  }

  def updateDelegateEntity(delegate: DelegateEntity): Future[DelegateEntity] = {
    val newDelegate = delegate.copy(updatedAt = new Timestamp(System.currentTimeMillis()))
    db.run(delegates.filter(_.id === delegate.id).update(newDelegate)).map(_ => newDelegate)
  }

  def upsertDelegate(delegate: DelegateEntity): Future[DelegateEntity] = {
    if(delegate.id.isDefined) { updateDelegateEntity(delegate) }
    else { insertDelegate(delegate) }
  }

  def getDelegateEntityById(id: Int): Future[DelegateEntity] = {
    db.run(delegates.filter(_.id === id).result.head)
  }

  def getDelegateEntitiesByCompanyId(companyId: Int): Future[List[DelegateEntity]] = {
    db.run(delegates.filter(_.companyId === companyId).result).map(_.toList)
  }

  def deleteDelegateById(id: Int): Future[DelegateEntity] = {
    val deletedDelegate = getDelegateEntityById(id)
    db.run(delegates.filter(_.id === id).delete)
    deletedDelegate
  }

  //FILTERS
  def searchDelegateEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[DelegateEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        delegateQry(companyId).filter{_.name.like(s)}
      }.getOrElse(delegateQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( delegateList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = delegateList)
          )
        )
  }
}
