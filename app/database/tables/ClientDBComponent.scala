package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ClientEntity(
  id: Option[Int] = None,
  companyId: Int,
  contactProfileId: Int,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()))


trait ClientDBComponent extends DBComponent {
    this: DBComponent 
    with  CompanyDBComponent
    with  ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val clients = TableQuery[ClientTable]
  
  class ClientTable(tag: Tag) extends Table[ClientEntity](tag, "tbl_client") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def contactProfileId = column[Int]("contact_profile_id")
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))

    def fkProfileId = foreignKey("fk_client_contact_profile", contactProfileId, contactProfiles)(_.id)
    def fkCompanyId = foreignKey("fk_client_company", companyId, companies)(_.id)

    def * = (id.?, companyId, contactProfileId, createdAt)<>(ClientEntity.tupled, ClientEntity.unapply)
  }

  //JOINS 
  //(ClientEntity, ContactProfileEntity)
  def clientWithProfile = clients join contactProfiles on (_.contactProfileId === _.id)


  //CRUD 
  def insertClient(client: ClientEntity): Future[ClientEntity] = {
      db.run((clients returning clients.map(_.id) into ((client,id) => client.copy(id=Some(id)))) += client)
  }

  def getClientEntityById(id: Int): Future[ClientEntity] = {
    db.run(clients.filter(_.id === id).result.head)
  }

  def getClientWithProfileById(id: Int): Future[(ClientEntity, ContactProfileEntity)] = {
    db.run(clientWithProfile.filter(_._1.id === id).result.head)
  }

  def updateClientEntity(client: ClientEntity): Future[ClientEntity] = {
    db.run(clients.filter(_.id === client.id).update(client))
                    .map(num => client) 
  }

  def deleteClientById(id: Int): Future[(ClientEntity, ContactProfileEntity)] = {
    val deleted = getClientWithProfileById(id)
    db.run(clients.filter(_.id === id).delete)
    deleted
  } 

  //TODO:Pagination
  //FILTERS
  def getAllClientsByCompanyId(companyId: Int): Future[List[ClientEntity]] = {
    db.run(clients.filter(_.companyId === companyId).result).map(_.toList)
  }

  def getAllClientsWithContactProfileByCompanyId(companyId: Int): Future[List[(ClientEntity, ContactProfileEntity)]] = {
    db.run(clientWithProfile.filter(_._1.companyId === companyId).result).map(_.toList)
  }
}

