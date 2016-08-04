package database

import javax.inject._

import models.{CalendarItem, MailToSend, ExchangeMail}
import utils.ews.{EwsAuthUtil, EwsCalendarUtil, EwsMailUtil}
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


import scala.concurrent.Future

class ExchangeRepository @Inject()(ewsAuth: EwsAuthUtil, ewsMail:EwsMailUtil, ewsCalendar: EwsCalendarUtil) {
  import database.gen.current.dao._

  def getCalendarItemsByMailboxId(mailboxId: Int, startDate: Long, endDate: Long):Future[List[CalendarItem]] = {
    getMailboxEntityById(mailboxId).map{res =>
            val service = ewsAuth.tryToLogin(res.server, res.login, res.password)
            ewsCalendar.findAppointments(service,startDate, endDate).asScala.toList
    }
  }

  //Unit because of EWS. It returns nothing when you send mail.
  def sendMail(mailboxId: Int, mail: MailToSend):Future[Unit] = {
    getMailboxEntityById(mailboxId).map{res =>
      val service = ewsAuth.tryToLogin(res.server, res.login, res.password)
      ewsMail.send(service, mail)
    }
  }

  def getSentMail(mailboxId: Int, pageNr: Int, pageSize: Int):Future[List[ExchangeMail]] = {
   getMailboxEntityById(mailboxId).map{res =>
      val service = ewsAuth.tryToLogin(res.server, res.login, res.password)
      ewsMail.getSentMail(service, mailboxId, pageNr, pageSize).asScala.toList
    }
  }
}
