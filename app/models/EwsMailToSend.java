package models;

public class EwsMailToSend {
    public String subject;
    public String body;
    public String[] to;

    public EwsMailToSend(String subject, String body, String[] to) {
        this.subject = subject;
        this.body = body;
        this.to = to;
    }
}
