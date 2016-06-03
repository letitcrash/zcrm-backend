package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class UnionEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  description: Option[String] = None,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait UnionDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent => 

  import dbConfig.driver.api._

  val unions = TableQuery[UnionTable]
  
  class UnionTable(tag: Tag) extends Table[UnionEntity](tag, "tbl_union") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def description = column[String]("description", Nullable)
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null"))

    def fkCompanyId = foreignKey("fk_union_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, companyId, name, description.?, recordStatus, createdAt, updatedAt)<>(UnionEntity.tupled, UnionEntity.unapply)
  }


  def unionQry(companyId: Int) = {
    unions.filter(u =>(u.companyId === companyId && 
                       u.recordStatus === RowStatus.ACTIVE))
  }
  //CRUD UnionEntity
  def insertUnion(union: UnionEntity): Future[UnionEntity] = {
      db.run((unions returning unions.map(_.id) into ((union,id) => union.copy(id=Some(id)))) += union)
  }

  def getUnionEntityById(id: Int): Future[UnionEntity] = {
    db.run(unions.filter(u =>(u.id === id && 
                              u.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateUnionEntity(union: UnionEntity): Future[UnionEntity] = {
      db.run(unions.filter(u =>(u.id === union.id && 
                                u.recordStatus === RowStatus.ACTIVE))
                .update(union)
                    .map{num => if(num != 0) union 
                                else throw new Exception("Can't update union, is it deleted?")})
  }

  def softDeleteUnionById(id: Int): Future[UnionEntity] = {
    getUnionEntityById(id).flatMap(res =>
        updateUnionEntity(res.copy(recordStatus = RowStatus.DELETED, 
                          updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getUnionEntitiesByCompanyId(companyId: Int): Future[List[UnionEntity]] = {
    db.run(unions.filter(u => (u.companyId === companyId && 
                               u.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def searchUnionEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[UnionEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        unionQry(companyId).filter{_.name.like(s)}
      }.getOrElse(unionQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( unionList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = unionList)
          )
        )
  }
}

