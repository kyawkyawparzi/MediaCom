package pmt.kyawkyaw.myapp.mediacom.notification;

public class Token {
    /*
    need to be create token that allow connection and send notification
    form server to from client to exact client's device token , performed to received
    message notification from specific device
    Id get from gcm
     */
    String token;

    public Token(String token) {
        this.token = token;
    }
    public Token() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
