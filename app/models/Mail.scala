package models

case class Mail(
  subject: Option[String] = None, 
  fromEmail: Option[String] = None,
  fromName: Option[String] = None,
  toEmail: Option[String] = None,
  toName: Option[String] = None,
  body: Option[String] = None
)
