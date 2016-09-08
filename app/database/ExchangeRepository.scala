package database

import javax.inject._

import models.{CalendarItem, MailToSend, ExchangeMail}
import utils.ews.{EwsAuthUtil, EwsCalendarUtil, EwsMailUtil}
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import play.api.Logger
import microsoft.exchange.webservices.data.core.ExchangeService
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion
import microsoft.exchange.webservices.data.credential.WebCredentials
import microsoft.exchange.webservices.data.search.ItemView
import microsoft.exchange.webservices.data.core.service.folder.Folder
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName
import java.net.URI

import scala.collection.JavaConversions._
import microsoft.exchange.webservices.data.search.filter.SearchFilter
import microsoft.exchange.webservices.data.property.definition.PropertyDefinitionBase
import microsoft.exchange.webservices.data.property.complex.FolderId
import microsoft.exchange.webservices.data.core.PropertySet

class ExchangeRepository @Inject()(ewsAuth: EwsAuthUtil, ewsMail:EwsMailUtil, ewsCalendar: EwsCalendarUtil) {
  import database.gen.current.dao._
  
  def getCalendarItemsByMailboxId(
      mailboxId: Int,
      startDate: Long,
      endDate: Long): Future[List[CalendarItem]] = {
    getMailboxEntityById(mailboxId).map { res =>
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
