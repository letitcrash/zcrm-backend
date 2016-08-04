package utils.converters
import database.tables.{ExchangeODSMailEntity, ExchangeSavedMailEntity}
import models._


object MailConverter {
  implicit class EntityToODSMail(entity: ExchangeODSMailEntity){
      def asMailFromODS: ExchangeMail = {
               ExchangeMail(id = entity.id,
                            extId = Some(entity.extId),
                            conversationExtId = Some(entity.conversationExtId),
                            mailboxId = Some(entity.mailboxId),
                            sender = Some(entity.sender),
                            receivedBy = Some(entity.receivedBy),
                            ccRecipients = Some(entity.ccRecipients),
                            bccRecipients = Some(entity.bccRecipients),
                            subject = Some(entity.subject),
                            body = Some(entity.body),
                            importance = Some(entity.importance),
                            attachments = Some(entity.attachments),
                            size = Some(entity.size),
                            received = Some(entity.received)
              )
      }
  }

  implicit class MailToODSEntity(mail: ExchangeMail){
    def asODSEntity: ExchangeODSMailEntity = {
      ExchangeODSMailEntity(id = mail.id,
                            extId = mail.extId.get,
                            conversationExtId = mail.conversationExtId.get,
                            mailboxId = mail.mailboxId.get,
                            sender = mail.sender.get,
                            receivedBy = mail.receivedBy.get,
                            ccRecipients = mail.ccRecipients.getOrElse("No cc"),
                            bccRecipients =   mail.bccRecipients.getOrElse("No bcc"),
                            subject = mail.subject.get,
                            body = mail.body.get,
                            importance = mail.importance.getOrElse("Normal"),
                            attachments = mail.attachments.getOrElse("No attachments"),
                            size = mail.size.getOrElse(0),
                            received = mail.received.get)
    }
  }

  implicit class EntityToSavedMail(entity: ExchangeSavedMailEntity){
      def asMailFromSaved: ExchangeMail = {
               ExchangeMail(id = entity.id,
                            extId = Some(entity.extId),
                            conversationExtId = Some(entity.conversationExtId),
                            sender = Some(entity.sender),
                            receivedBy = Some(entity.receivedBy),
                            ccRecipients = Some(entity.ccRecipients),
                            bccRecipients = Some(entity.bccRecipients),
                            subject = Some(entity.subject),
                            body = Some(entity.body),
                            importance = Some(entity.importance),
                            attachments = Some(entity.attachments),
                            size = Some(entity.size),
                            received = Some(entity.received)
              )
      }
  }

  implicit class MailToSavedEntity(mail: ExchangeMail){
    def asSavedEntity: ExchangeSavedMailEntity = {
      ExchangeSavedMailEntity(id = mail.id,
                              extId = mail.extId.get,
                              conversationExtId = mail.conversationExtId.get,
                              sender = mail.sender.get,
                              receivedBy = mail.receivedBy.get,
                              ccRecipients = mail.ccRecipients.getOrElse("No cc"),
                              bccRecipients =   mail.bccRecipients.getOrElse("No bcc"),
                              subject = mail.subject.get,
                              body = mail.body.get,
                              importance = mail.importance.getOrElse("Normal"),
                              attachments = mail.attachments.getOrElse("No attachments"),
                              size = mail.size.getOrElse(0),
                              received = mail.received.get)
    }
  }

}
