package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import models.MailToSend;
import scala.collection.JavaConversions;
import java.util.Date;

public class EwsMailUtil {

  public void send(ExchangeService service, MailToSend mail) throws Exception {
    EmailMessage msg = new EmailMessage(service);
    msg.setSubject(mail.subject());
    msg.setBody(MessageBody.getMessageBodyFromText(mail.body()));
    for (String recipient : JavaConversions.asJavaCollection(mail.to())) {msg.getToRecipients().add(recipient);}
    Date date = new Date();
    msg.getDateTimeCreated().setTime(date.getTime());
  }
}
