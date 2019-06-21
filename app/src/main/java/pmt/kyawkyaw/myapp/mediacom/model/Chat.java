package pmt.kyawkyaw.myapp.mediacom.model;

import java.util.Date;

public class Chat {
    String sender;
    String receiver;
    String message;
    boolean isseen;
    String sendtime;

    public Chat(String sender, String receiver, String message,String sendtime,boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen=isseen;
        this.sendtime=sendtime;
    }

    public Chat(){

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }
}
