package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class ShiftEntity(
  id: Option[Int] = None,
  companyId: Int,
  name: String,
  recordStatus: Int = RowStatus.ACTIVE,
  createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
  updatedAt: Timestamp = new Timestamp(System.currentTimeMillis()))

trait ShiftDBComponent extends DBComponent {
    this: DBComponent 
    with CompanyDBComponent => 

  import dbConfig.driver.api._

  val shifts = TableQuery[ShiftTable]
  
  class ShiftTable(tag: Tag) extends Table[ShiftEntity](tag, "tbl_shift") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyId = column[Int]("company_id")
    def name = column[String]("name", O.SqlType("VARCHAR(255)"))
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.SqlType("timestamp not null default CURRENT_TIMESTAMP"))
    def updatedAt = column[Timestamp]("updated_at", O.SqlType("timestamp not null"))

    def fkCompanyId = foreignKey("fk_shift_company", companyId, companies)(_.id)

    def * = (id.?, companyId, name, recordStatus, createdAt, updatedAt)<>(ShiftEntity.tupled, ShiftEntity.unapply)
  }


  def shiftQry(companyId: Int) = {
    shifts.filter(s =>(s.companyId === companyId && 
                       s.recordStatus === RowStatus.ACTIVE))
  }

  //CRUD ShiftEntity
  def insertShift(shift: ShiftEntity): Future[ShiftEntity] = {
      db.run((shifts returning shifts.map(_.id) into ((shift,id) => shift.copy(id=Some(id)))) += shift)
  }

  def getShiftEntityById(id: Int): Future[ShiftEntity] = {
    db.run(shifts.filter(s =>(s.id === id && 
                              s.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updateShiftEntity(shift: ShiftEntity): Future[ShiftEntity] = {
      db.run(shifts.filter(s =>(s.id === shift.id && 
                                s.recordStatus === RowStatus.ACTIVE)).update(shift)
                    .map{num => if(num != 0) shift 
                                else throw new Exception("Can't update shift, is it deleted?")})
  }

  def softDeleteShiftById(id: Int): Future[ShiftEntity] = {
    getShiftEntityById(id).flatMap(res =>
        updateShiftEntity(res.copy(recordStatus = RowStatus.DELETED, 
                                   updatedAt = new Timestamp(System.currentTimeMillis()))))
  } 

  //FILTERS
  def getShiftEntitiesByCompanyId(companyId: Int): Future[List[ShiftEntity]] = {
    db.run(shifts.filter(s => (s.companyId === companyId && 
                               s.recordStatus === RowStatus.ACTIVE)).result).map(_.toList)
  }

  def searchShiftEntitiesByName(companyId: Int, pageSize: Int, pageNr: Int, searchTerm: Option[String] = None): Future[PagedDBResult[ShiftEntity]] = {
    val baseQry = searchTerm.map { st =>
        val s = "%" + st + "%"
        shiftQry(companyId).filter{_.name.like(s)}
      }.getOrElse(shiftQry(companyId))  

    val pageRes = baseQry
      .sortBy(_.name.asc)
      .drop(pageSize * (pageNr - 1))
      .take(pageSize)

    db.run(pageRes.result).flatMap( shiftList => 
        db.run(baseQry.length.result).map( totalCount => 
         PagedDBResult(
            pageSize = pageSize,
            pageNr = pageNr,
            totalCount = totalCount,
            data = shiftList)
          )
        )
  }
}

