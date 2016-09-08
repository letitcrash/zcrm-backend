package models

import java.sql.Date

case class MailboxRf(
    id: Int,
    userId: Int,
    server: String,
    login: String,
    password: String,
    created: Option[Date],
    updated: Option[Date],
    syncState: String
)