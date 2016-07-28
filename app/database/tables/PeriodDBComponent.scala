package database.tables

import java.sql.Timestamp
import models.RowStatus
import slick.model.ForeignKeyAction._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.profile.SqlProfile.ColumnOption.Nullable
import database.PagedDBResult

case class PeriodEntity(
  id: Option[Int] = None,
  previousPeriodId: Option[Int] = None,
  userId: Int,
  start: Timestamp,
  end: Option[Timestamp] = None,
  status: Int, //0 - Started, 1 - Paused, 2 - Stopped 
  recordStatus: Int = RowStatus.ACTIVE)

trait PeriodDBComponent extends DBComponent {
    this: DBComponent 
    with UserDBComponent => 

  import dbConfig.driver.api._

  val periods = TableQuery[PeriodTable]
  
  class PeriodTable(tag: Tag) extends Table[PeriodEntity](tag, "tbl_period") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def previousPeriodId = column[Int]("previous_period_id", Nullable)
    def userId = column[Int]("user_id")
    def start = column[Timestamp]("start")
    def end = column[Timestamp]("end", Nullable)
    def status =column[Int]("status")
    def recordStatus = column[Int]("record_status", O.Default(RowStatus.ACTIVE))

    def fkUserId = foreignKey("fk_period_user", userId, users)(_.id)

    def * = (id.?, previousPeriodId.?, userId, start, end.?, status, recordStatus)<>(PeriodEntity.tupled, PeriodEntity.unapply)
  }

  //CRUD
  def insertPeriod(period: PeriodEntity): Future[PeriodEntity] = {
    db.run((periods returning periods.map(_.id) into ((period,id) => period.copy(id=Some(id)))) += period)
  }

  def getPeriodEntityById(id: Int): Future[PeriodEntity] = {
    db.run(periods.filter(s =>(s.id === id && 
                              s.recordStatus === RowStatus.ACTIVE)).result.head)
  }

  def updatePeriodEntity(period: PeriodEntity): Future[PeriodEntity] = {
      db.run(periods.filter(s =>(s.id === period.id && 
                                s.recordStatus === RowStatus.ACTIVE)).update(period)
                    .map{num => if(num != 0) period 
                                else throw new Exception("Can't update period, is it deleted?")})
  }

  def softDeletePeriodById(id: Int): Future[PeriodEntity] = {
    getPeriodEntityById(id).flatMap(res =>
        updatePeriodEntity(res.copy(recordStatus = RowStatus.DELETED)))
  } 
  
}

