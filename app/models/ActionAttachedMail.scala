package models

case class ActionAttachedMail(id: Option[Int] = None,
                              actionId: Int,
                              mailId: Int)
