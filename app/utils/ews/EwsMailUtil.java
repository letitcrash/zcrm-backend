package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import models.ExchangeMail;
import models.MailToSend;
import scala.Option;
import scala.Some;
import scala.collection.JavaConversions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EwsMailUtil {

  public void send(ExchangeService service, MailToSend mail) throws Exception {
    EmailMessage msg = new EmailMessage(service);
    msg.setSubject(mail.subject());
    msg.setBody(MessageBody.getMessageBodyFromText(mail.body()));
    for (String recipient : JavaConversions.asJavaCollection(mail.to())) {msg.getToRecipients().add(recipient);}
    Date date = new Date();
    msg.getDateTimeCreated().setTime(date.getTime());
  }

  public List<ExchangeMail> getSentMail(ExchangeService service, int mailboxId, int pageNr, int pageSize) throws Exception {
    List<ExchangeMail> result = new ArrayList<>();
    int offset = (pageNr-1)*pageSize;
    FindItemsResults<Item> findResults = getItemsFromWellKnownFolder(WellKnownFolderName.SentItems, service, offset, pageSize);
    service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action
     for (Item item : findResults) {
        EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
        String cc = " ", bcc = " ";
        for (EmailAddress address : message.getCcRecipients()) {cc += address.getAddress() + ",";}
        for (EmailAddress address : message.getBccRecipients()) {bcc += address.getAddress() + ",";}
        ExchangeMail msg = new ExchangeMail(Option.apply(null), // id
                                            new Some<>(message.getId().toString()), // extId
                                            new Some<>(message.getConversationId().toString()), // conversationExtId
                                            new Some<>(mailboxId), // mailboxId
                                            new Some<>(message.getFrom().getAddress()), // sender
                                            new Some<>(message.getReceivedBy().getAddress()), // receivedBy
                                            new Some<>(cc), // ccRecipients
                                            new Some<>(bcc), // bccRecipients
                                            new Some<>(message.getSubject()), // subject
                                            new Some<>(message.getBody().toString()), // body
                                            new Some<>(message.getImportance().toString()), // importance
                                            new Some<>("There are must be attachments, but no"), // attachments
                                            new Some<>(item.getSize()), // size
                                            new Some<>(new Timestamp(item.getDateTimeReceived().getTime()))); // received
        result.add(msg);
     }
    return result;
  }

  private FindItemsResults<Item> getItemsFromWellKnownFolder(WellKnownFolderName folder, ExchangeService service, int offset, int pageSize) throws Exception {
    ItemView view = new ItemView(pageSize, offset);
    return service.findItems(folder, view);
  }
}
