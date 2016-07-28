package database

import models.Employee

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.{Period, PagedResult}
import play.api.Logger
import utils.converters.PeriodConverter._


object PeriodDBRepository {
  import database.gen.current.dao.dbConfig.driver.api._
  import database.gen.current.dao._

  /*def createPeriod(period: Period): Future[Period] = {
    insertPeriod(period.asPeriodEntity)
          .map(inserted => inserted.asPeriod)
  }

  def updatePeriod(period: Period): Future[Period] = {
    updatePeriodEntity(period.asPeriodEntity)
          .map(updated => updated.asPeriod)
  }

  def deletePeriod(periodId: Int): Future[Period] = {
    softDeletePeriodById(periodId)
          .map(deleted => deleted.asPeriod)
  }

  def getPeriodById(id: Int): Future[Period] = {
    getPeriodEntityById(id).map(period => period.asPeriod)
  }
*/

}
