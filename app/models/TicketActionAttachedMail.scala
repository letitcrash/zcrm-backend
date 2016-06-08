package models

case class TicketActionAttachedMail(id: Option[Int] = None,
                                    actionId: Int,
                                    mailId: Int)
