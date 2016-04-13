package models;


public class EwsMailToReply {
    public String replyMailId;
    public String replyBody;

    public EwsMailToReply(String replyMailId, String replyBody) {
        this.replyMailId = replyMailId;
        this.replyBody = replyBody;
    }
}
