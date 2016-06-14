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
    with  ContactProfileDBComponent =>

  import dbConfig.driver.api._

  val clients = TableQuery[ClientTable]
  
  class ClientTable(tag: Tag) extends Table[ClientEntity](tag, "tbl_client") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def contactProfileId = column[Int]("contact_profile_id")
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))

    def fkCompanyId = foreignKey("fk_client_contact_profile", contactProfileId, contactProfiles)(_.id)

    def * = (id.?, companyId, contactProfileId, createdAt)<>(ClientEntity.tupled, ClientEntity.unapply)
  }

  //JOINS 


  //CRUD 


  //FILTERS

}

