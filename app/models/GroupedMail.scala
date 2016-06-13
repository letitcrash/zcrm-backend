package models

case class GroupedMail(conversationId: String,
                       mails: List[ExchangeMail])
