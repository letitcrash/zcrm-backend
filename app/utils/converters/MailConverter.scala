package utils.converters
import models._

object MailConverter {

  implicit class EwsMailToInboxMail(ewsMail: InboxMessage) {
    def asInboxMail: InboxMail = {
      InboxMail(
      id = Some(ewsMail.id.getUniqueId),
      subject = Some(ewsMail.subject),
      fromEmail = Some(ewsMail.fromEmail),
      fromName = Some(ewsMail.fromName),
      toEmail = Some(ewsMail.toEmail),
      toName = Some(ewsMail.toName),
      body = Some(ewsMail.body),
      isRead = Some(ewsMail.isRead)
    )
    }

  }
  implicit class EwsMailToOutboxMail(ewsMail: OutboxMessage) {
    def asOutboxMail: OutboxMail = {
      OutboxMail(
        id = Some(ewsMail.id.getUniqueId),
        subject = Some(ewsMail.subject),
        fromEmail = Some(ewsMail.fromEmail),
        fromName = Some(ewsMail.fromName),
        toEmail = Some(ewsMail.toEmail),
        toName = Some(ewsMail.toName),
        body = Some(ewsMail.body)
      )
    }

  }

}
