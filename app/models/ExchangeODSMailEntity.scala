package models

import java.sql.Timestamp

case class ExchangeODSMailEntity(
    id: Option[Int] = None,
    mailboxId: Int,
    extId: String,
    conversationExtId: String,
    sender: String,
    receivedBy: String,
    ccRecipients: String,
    bccRecipients: String,
    subject: String,
    body: String,
    importance: String,
    attachments: String,
    size: Int,
    received: Timestamp)