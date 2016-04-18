package models

case class Mailbox(id: Option[Int] = None,
                   userId: Int,
                   server: String,
                   login: String,
                   password: String)
