package models

case class InboxMail(id: Option[String] = None,
                     subject: Option[String] = None,
                     fromEmail: Option[String] = None,
                     fromName: Option[String] = None,
                     toEmail: Option[String] = None,
                     toName: Option[String] = None,
                     body: Option[String] = None,
                     isRead: Option[Boolean]= None)

case class OutboxMail(id: Option[String] = None,
                      subject: Option[String] = None,
                      fromEmail: Option[String] = None,
                      fromName: Option[String] = None,
                      toEmail: Option[String] = None,
                      toName: Option[String] = None,
                      body: Option[String] = None)
