package database.tables

import java.sql.Timestamp
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
    def name = column[String]("name")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def fkCompany = foreignKey("fk_delegate_company", companyId, companies)(_.id)

    override def * = (id.?, companyId, name, createdAt, updatedAt) <> (DelegateEntity.tupled, DelegateEntity.unapply)
  }

  def insertDelegate(delegate: DelegateEntity): Future[DelegateEntity] = {
    val createdAt = new Timestamp(System.currentTimeMillis())

    db.run(
      (delegates returning delegates.map(_.id) into ((delegate, id) => delegate.copy(id = Some(id))))
        += delegate.copy(createdAt = createdAt))
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


}
