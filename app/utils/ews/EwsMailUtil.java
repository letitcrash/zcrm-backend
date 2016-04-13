package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import models.*;

import java.util.ArrayList;

public class EwsMailUtil {

    public InboxMessage[] getInboxMail(ExchangeService service, int pageNr, int pageSize) throws Exception {
        ArrayList<InboxMessage> result = new ArrayList<>();

        int offset = (pageNr-1)*pageSize;
        FindItemsResults<Item> findResults = getItemsFromWellKnownFolder(WellKnownFolderName.Inbox, service, offset, pageSize);

        service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action

        for (Item item : findResults) {
            EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
            InboxMessage msg = new InboxMessage(item.getId(), item.getSubject(),
                                                    message.getFrom().getAddress(), message.getFrom().getName(),
                                                    message.getReceivedBy().getAddress(), message.getReceivedBy().getName(),
                                                    item.getBody().toString(), message.getIsRead() ,
                                                    item.getDateTimeReceived(), item.getAttachments());
            result.add(msg);
        }
        return result.toArray(new InboxMessage[result.size()]);
    }

    public OutboxMessage[] getSentMail(ExchangeService service, int pageNr, int pageSize) throws Exception {
        ArrayList<OutboxMessage> result = new ArrayList<>();

        int offset = (pageNr-1)*pageSize;
        FindItemsResults<Item> findResults = getItemsFromWellKnownFolder(WellKnownFolderName.SentItems, service, offset, pageSize);

        service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action

        for (Item item : findResults) {
            EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
            EmailAddress recipient = message.getToRecipients().getPropertyAtIndex(0);
            OutboxMessage msg = new OutboxMessage(item.getId(), item.getSubject(),
                                                                                       message.getFrom().getAddress(), message.getFrom().getName(),
                                                                                       recipient.getAddress(), recipient.getName(),
                                                                                       item.getBody().toString(), item.getDateTimeSent(), item.getAttachments());
            result.add(msg);
        }
        return result.toArray(new OutboxMessage[result.size()]);
    }


		private FindItemsResults<Item> getItemsFromWellKnownFolder(WellKnownFolderName folder, ExchangeService service, int offset, int pageSize) throws Exception {
			  ItemView view = new ItemView(pageSize, offset);
              return service.findItems(folder, view);
		}


    public void sendMessage(ExchangeService service, MessageToSend message) throws Exception {
        EmailMessage msg = new EmailMessage(service);
        msg.setSubject(message.getSubject());
        msg.setBody(MessageBody.getMessageBodyFromText(message.getBody()));
        for (String recipient : message.getTo()) {msg.getToRecipients().add(recipient);}
        msg.getDateTimeCreated().setTime(message.getDate().getTime());
        msg.getDateTimeSent().setTime(message.getDate().getTime());

        msg.sendAndSaveCopy();
    }

    public void replyToMessage(ExchangeService service, String replyBody, String messageToReplyId) throws Exception {
        EmailMessage msg = EmailMessage.bind(service, new ItemId(messageToReplyId),PropertySet.FirstClassProperties );
        ResponseMessage reply = msg.createReply(false);
        reply.setBodyPrefix(MessageBody.getMessageBodyFromText(replyBody));
        reply.sendAndSaveCopy();
    }
}
