package models;


import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.ItemId;

import java.util.Date;

public class EwsInboxMail {
    public ItemId id;
    public String subject;
    public String fromEmail;
    public String fromName;
    public String toEmail;
    public String toName;
    public String body;
    public Boolean isRead;
    public Date date;
    public AttachmentCollection attachments;

    public EwsInboxMail() {}

    public EwsInboxMail(ItemId id, String subject, String fromEmail, String fromName, String toEmail,
                        String toName, String body, Boolean isRead, Date date, AttachmentCollection attachments) {
        this.id = id;
        this.subject = subject;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.toEmail = toEmail;
        this.toName = toName;
        this.body = body;
        this.isRead  = isRead;
        this.date = date;
        this.attachments = attachments;
    }
}
