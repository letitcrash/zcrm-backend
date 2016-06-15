package database

import java.sql.Timestamp

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.ews.{EwsAuthUtil, EwsCalendarUtil}
import scala.collection.JavaConverters._

import scala.concurrent.Future


object ExchangeRepository {
  import database.gen.current.dao._

  def getCalendarItemsByMailboxId(mailboxId: Int, startDate: Timestamp, endDate: Timestamp):Future[List[CalendarItem]] = {
    val ewsAuth = new EwsAuthUtil()
    val ewsCalendar = new EwsCalendarUtil()
    getMailboxEntityById(mailboxId).map{res =>
            val service = ewsAuth.tryToLogin(res.server, res.login, res.password)
            ewsCalendar.findAppointments(service,startDate, endDate).asScala.toList
    }
  }
}
