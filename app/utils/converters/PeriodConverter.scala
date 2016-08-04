package utils.converters

import models._
import database.tables.PeriodEntity

object PeriodConverter {

  implicit class PeriodToEntity(period: Period) {
    def asPeriodEntity: PeriodEntity  = {
      PeriodEntity(id = period.id,
                   userId = -1, //FIXME
                   start = period.start,
                   end = period.end,
                   status = period.status)
    }

  }
  implicit class EntityToPeriod(periodEntt: PeriodEntity) {
    def asPeriod: Period = {
      Period(id = periodEntt.id,
             start = periodEntt.start,
             end = periodEntt.end,
             status = periodEntt.status)
      }
  }

}
