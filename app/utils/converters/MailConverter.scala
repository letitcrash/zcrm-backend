package utils.converters
import models._

object MailConverter {

  implicit class EwsMailtoMail(ewsMail: MessageToReceive) {
    def asMail: Mail = {
    Mail(
      subject = Some(ewsMail.getSubject),
      fromEmail = Some(ewsMail.getFromEmail),
      fromName = Some(ewsMail.getFromName),
      toEmail = Some(ewsMail.getToEmail),
      toName = Some(ewsMail.getToName),
      body = Some(ewsMail.getBody)
    )
    }
  }

}
