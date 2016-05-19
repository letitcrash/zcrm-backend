package models

import java.sql.Timestamp

case class ExchangeMail(extId: Option[String] = None,
                        conversationExtId: Option[String] = None,
                        mailboxId: Option[Int] = None,
                        sender: Option[String] = None,
                        receivedBy: Option[String] = None,
                        ccRecipients: Option[String] = None,
                        bccRecipients: Option[String] = None,
                        subject: Option[String] = None,
                        body: Option[String] = None,
                        importance: Option[String] = None,
                        attachments: Option[String] = None,
                        size: Option[Int] = None,
                        received: Option[Timestamp] = None)