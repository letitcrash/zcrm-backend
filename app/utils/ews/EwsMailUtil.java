package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import models.*;

import java.util.ArrayList;

public class EwsMailUtil {

    public EwsInboxMail[] getInboxMail(ExchangeService service, int pageNr, int pageSize) throws Exception {
        ArrayList<EwsInboxMail> result = new ArrayList<>();

        int offset = (pageNr-1)*pageSize;
        FindItemsResults<Item> findResults = getItemsFromWellKnownFolder(WellKnownFolderName.Inbox, service, offset, pageSize);

        service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action

        for (Item item : findResults) {
            EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
            EwsInboxMail msg = new EwsInboxMail(item.getId(), item.getSubject(),
                                                message.getFrom().getAddress(), message.getFrom().getName(),
                                                message.getReceivedBy().getAddress(), message.getReceivedBy().getName(),
                                                item.getBody().toString(), message.getIsRead() ,
                                                item.getDateTimeReceived(), item.getAttachments());
            result.add(msg);
        }
        return result.toArray(new EwsInboxMail[result.size()]);
    }

    public EwsSentMail[] getSentMail(ExchangeService service, int pageNr, int pageSize) throws Exception {
        ArrayList<EwsSentMail> result = new ArrayList<>();

        int offset = (pageNr-1)*pageSize;
        FindItemsResults<Item> findResults = getItemsFromWellKnownFolder(WellKnownFolderName.SentItems, service, offset, pageSize);

        service.loadPropertiesForItems(findResults, PropertySet.FirstClassProperties); //Need to load properties before any other action

        for (Item item : findResults) {
            EmailMessage message = EmailMessage.bind(service, new ItemId(item.getId().getUniqueId()));
            EmailAddress recipient = message.getToRecipients().getPropertyAtIndex(0);
            EwsSentMail msg = new EwsSentMail(item.getId(), item.getSubject(),
                                                  message.getFrom().getAddress(), message.getFrom().getName(),
                                                  recipient.getAddress(), recipient.getName(),
                                                  item.getBody().toString(), item.getDateTimeSent(), item.getAttachments());
            result.add(msg);
        }
        return result.toArray(new EwsSentMail[result.size()]);
    }

    public String getMailBodyById(ExchangeService service, String uniqueId) throws Exception{
			  Item item = Item.bind(service, new ItemId(uniqueId));
			  return item.getBody().toString();
		}

    public void setMailAsRead(ExchangeService service, String uniqueId) throws Exception {
        EmailMessage message = EmailMessage.bind(service, new ItemId(uniqueId));
        message.setIsRead(true);
        message.update(ConflictResolutionMode.AutoResolve);
    }

    public void sendMail(ExchangeService service, EwsMailToSend message) throws Exception {
        EmailMessage msg = new EmailMessage(service);
        msg.setSubject(message.subject);
        msg.setBody(MessageBody.getMessageBodyFromText(message.body));
        for (String recipient : message.to) {msg.getToRecipients().add(recipient);}
        msg.sendAndSaveCopy();
    }

    public void replyToMail(ExchangeService service, EwsMailToReply replyMail) throws Exception {
        EmailMessage msg = EmailMessage.bind(service, new ItemId(replyMail.replyMailId),PropertySet.FirstClassProperties );
        ResponseMessage reply = msg.createReply(false);
        reply.setBodyPrefix(MessageBody.getMessageBodyFromText(replyMail.replyBody));
        reply.sendAndSaveCopy();
    }

    public int getInboxMailCount(ExchangeService service, boolean isWatchInChildFolders, boolean isUnReadOnly) throws Exception {
        FolderId folderId = FolderId.getFolderIdFromWellKnownFolderName(WellKnownFolderName.Inbox);
        return getItemCountForFolder(service, folderId , isWatchInChildFolders, isUnReadOnly);
    }

    public int getSentMailCount(ExchangeService service, boolean isWatchInChildFolders) throws Exception{
        FolderId folderId = FolderId.getFolderIdFromWellKnownFolderName(WellKnownFolderName.SentItems);
        return getItemCountForFolder(service, folderId , isWatchInChildFolders, false);
    }



    private FindItemsResults<Item> getItemsFromWellKnownFolder(WellKnownFolderName folder, ExchangeService service, int offset, int pageSize) throws Exception {
        ItemView view = new ItemView(pageSize, offset);
        return service.findItems(folder, view);
    }

    private int getItemCountForFolder(ExchangeService service, FolderId folderId, boolean isWatchInChildFolders, boolean isUnReadOnly) throws Exception {
        int count = 0;
        FolderView viewFolders = new FolderView(Integer.MAX_VALUE);
        if (isWatchInChildFolders) {viewFolders.setTraversal(FolderTraversal.Deep);} //If needed to watch in subfloders - set filter
        viewFolders.setPropertySet(new PropertySet(BasePropertySet.IdOnly));//Load only ID's from Exchange request

        ItemView viewEmails = new ItemView(Integer.MAX_VALUE);
        viewEmails.setPropertySet(new PropertySet(BasePropertySet.IdOnly));//Load only ID's from Exchange request
        SearchFilter unreadFilter = null;
        //If it's inbox folder & needed to load unread messages only - set filter
        if (isUnReadOnly) {unreadFilter = new SearchFilter.SearchFilterCollection(LogicalOperator.And, new SearchFilter.IsEqualTo(EmailMessageSchema.IsRead, false));}

        //If unReadOnly - apply filter
        FindItemsResults<Item> findResults = isUnReadOnly ? service.findItems(folderId, unreadFilter, viewEmails) : service.findItems(folderId, viewEmails);
        count += findResults.getTotalCount();

        //If needed count from Root folder only - return value, else - get count from each folder in Root
        if(!isWatchInChildFolders){
            return count;
        } else{
            FindFoldersResults folders  = service.findFolders(folderId, viewFolders);
            for (Folder folder : folders) {
                findResults = service.findItems(folder.getId(), unreadFilter, viewEmails);
                count += findResults.getTotalCount();
            }
            return count;
        }
    }
}
