package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import models.MessageToReceive;
import models.MessageToSend;

import java.util.ArrayList;
import java.util.List;

public class EwsMailUtil {;

    public MessageToReceive[] getInboxMail(ExchangeService service, int page, int size) throws Exception {
        ArrayList<MessageToReceive> result = new ArrayList<>(size);

        int offset = page*size;
        ItemView view = new ItemView(size,offset);
        FindItemsResults<Item> findResults = service.findItems(WellKnownFolderName.Inbox, view);

        service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action

        for (Item item : findResults.getItems()) {
            EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
            MessageToReceive msg = new MessageToReceive(item.getId(), item.getSubject(), message.getFrom().getAddress(),
                    message.getFrom().getName(), "no-avatar",
                    message.getReceivedBy().getAddress(), message.getReceivedBy().getName(),
                    item.getBody().toString(), item.getDateTimeReceived(), item.getAttachments());
            result.add(msg);
        }
        return result.toArray(new MessageToReceive[size]);
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