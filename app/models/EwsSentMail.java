package models;


import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.ItemId;

import java.util.Date;

public class EwsSentMail {
    public ItemId id;
    public String subject;
    public String fromEmail;
    public String fromName;
    public String toEmail;
    public String toName;
    public String body;
    public Date date;
    public AttachmentCollection attachments;

    public EwsSentMail() {}

    public EwsSentMail(ItemId id, String subject, String fromEmail, String fromName, String toEmail,
                       String toName, String body, Date date, AttachmentCollection attachments) {
        this.id = id;
        this.subject = subject;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.toEmail = toEmail;
        this.toName = toName;
        this.body = body;
        this.date = date;
        this.attachments = attachments;
    }
}
