package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable

case class ShiftEntity(
  id: Option[Int] = None,
	companyId: Int,
	name: String,
  recordStatus: String = RowStatus.ACTIVE,
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
		def name = column[String]("name")
    def recordStatus = column[String]("record_status", O.Default(RowStatus.ACTIVE))
    def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))
    def updatedAt = column[Timestamp]("updated_at", O.Default(new Timestamp(System.currentTimeMillis())))

		def fkCompanyId = foreignKey("fk_shift_company", companyId, companies)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (id.?, companyId, name, recordStatus, createdAt, updatedAt)<>(ShiftEntity.tupled, ShiftEntity.unapply)
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
}

