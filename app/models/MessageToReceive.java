package models;


import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.ItemId;

import java.util.Date;

public class MessageToReceive {
    private ItemId id;
    private String subject;
    private String fromEmail;
    private String fromName;
    private String fromAvatarLink;
    private String toEmail;
    private String toName;
    private String body;
    private Date date;
    private AttachmentCollection attachments;

    public MessageToReceive() {}

    public MessageToReceive(ItemId id, String subject, String fromEmail, String fromName, String fromAvatarLink, String toEmail,
                                                        String toName, String body, Date date, AttachmentCollection attachments) {
        this.id = id;
        this.subject = subject;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.fromAvatarLink = fromAvatarLink;
        this.toEmail = toEmail;
        this.toName = toName;
        this.body = body;
        this.date = date;
        this.attachments = attachments;
    }

    public ItemId getId() {
        return id;
    }

    public void setId(ItemId id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAvatarLink() {
        return fromAvatarLink;
    }

    public void setFromAvatarLink(String fromAvatarLink) {
        this.fromAvatarLink = fromAvatarLink;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AttachmentCollection getAttachments() {
        return attachments;
    }

    public void setAttachments(AttachmentCollection attachments) {
        this.attachments = attachments;
    }
}
