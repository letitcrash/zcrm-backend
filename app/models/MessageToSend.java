package models;


import java.util.Date;

public class MessageToSend {
    private String subject;
    private String body;
    private String[] to;
    private Date date;

    public MessageToSend(String subject, String body, String[] to) {
        this.subject = subject;
        this.body = body;
        this.to = to;
        date = new Date();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
