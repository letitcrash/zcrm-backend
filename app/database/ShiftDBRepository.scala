package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Shift
import play.api.Logger
import utils.converters.ShiftConverter._


object ShiftDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  def createShift(shift: Shift, companyId: Int): Future[Shift] = {
    insertShift(shift.asShiftEntity(companyId))
          .map(inserted => inserted.asShift)
  }

  def updateShift(shift: Shift, companyId: Int): Future[Shift] = {
    updateShiftEntity(shift.asShiftEntity(companyId))
          .map(updated => updated.asShift)
  }

  def deleteShift(shiftId: Int): Future[Shift] = {
    softDeleteShiftById(shiftId)
          .map(deleted => deleted.asShift)
  }

  def getShiftById(id: Int): Future[Shift] = {
    getShiftEntityById(id).map(shift => shift.asShift)
  }

  def getShiftsByCompanyId(companyId: Int): Future[List[Shift]] = {
    getShiftEntitiesByCompanyId(companyId).map(list => list.map(_.asShift))
  } 
}
