package utils.converters

import models._
import database.tables.MailboxEntity

object MailboxConverter {

  implicit class MailboxToEntity(mailbox: Mailbox) {
    def asMailboxEntity: MailboxEntity  = {
      MailboxEntity(id = mailbox.id,
                    userId = mailbox.userId,
                    server = mailbox.server,
                    login = mailbox.login,
                    password = mailbox.password)
    }

  }
  implicit class EntityToMailbox(mailboxEntt: MailboxEntity) {
    def asMailbox: Mailbox = {
      Mailbox(id = mailboxEntt.id,
              userId = mailboxEntt.userId,
              server = mailboxEntt.server,
              login = mailboxEntt.login,
              password = mailboxEntt.password)
      }
  }

}
