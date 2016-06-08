package models

case class TicketActionAttachedFile(id: Option[Int] = None,
                                    actionId: Int,
                                    fileId: Int)
