package database.tables

import models.{UserLevels, RowStatus}
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import java.sql.Timestamp
import database.PagedDBResult


case class PositionEntity(id: Option[Int] = None,
                          companyId:  Int,
                          name: String,
                          recordStatus: Int = RowStatus.ACTIVE,
                          createdAt: Option[Timestamp] = Some(new Timestamp(System.currentTimeMillis())),
                          updatedAt: Option[Timestamp] = Some(new Timestamp(System.currentTimeMillis())))

trait PositionDBComponent extends DBComponent{
  this: DBComponent 
    with CompanyDBComponent =>

  import dbConfig.driver.api._

  val positions = TableQuery[PositionTable]

  class PositionTable(tag: Tag) extends Table[PositionEntity](tag, "tbl_position") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    //def comment = column[String]("comment")
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null"))

    def fkPositionCompany =
      foreignKey("fk_position_company", companyId, companies)(_.id)

    override def * =
      ( id.?, companyId, name, recordStatus, createdAt.?, updatedAt.?) <> (PositionEntity.tupled, PositionEntity.unapply)
  }

  def positionQry(companyId: Int) = {
    positions.filter(p =>(p.companyId === companyId && 
                          p.recordStatus === RowStatus.ACTIVE))
  }

  //CRUD Position
  def insertPosition(position: PositionEntity): Future[PositionEntity] = {
      val ts = Some(new Timestamp(System.currentTimeMillis()))
      val row = position.copy( updatedAt = ts)
        db.run((positions returning positions.map(_.id) 
          into ((position,id) => position.copy(id=Some(id)))) += row)
  }

  def updatePosition(position: PositionEntity):Future[PositionEntity] = {
    val newPosition = position.copy(updatedAt = Some(new Timestamp(System.currentTimeMillis())))
       db.run(positions.filter(_.id === position.id).update(newPosition))
                     .map(num => newPosition)
  }

  def getPositionEntityById(id: Int): Future[PositionEntity] = {
    db.run(positions.filter(_.id === id).result.head)
  }
 

  def deletePosition(id: Int): Future[PositionEntity] = {
    val deletedPosition = getPositionEntityById(id)
    db.run(positions.filter(_.id === id).delete)
    deletedPosition
  }


  //Position filters
   def getPositionEntitiesByCompanyId(companyId: Int): Future[List[PositionEntity]] = {
     db.run(positions.filter(_.companyId === companyId).result).map(_.toList)
   }

  def searchPositionEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[PositionEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        positionQry(companyId).filter{_.name.like(s)}
      }.getOrElse(positionQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( positionList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = positionList)
          )
        )
  }

}
