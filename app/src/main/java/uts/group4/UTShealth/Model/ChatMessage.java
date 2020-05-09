package uts.group4.UTShealth.Model;

import android.os.Message;

import java.util.Date;
import java.util.Map;

public class ChatMessage {

    private String messageText;
    public Map time;
    private String messageUser;




    public ChatMessage(String messageText, String messageUser, Map Time) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.time = time;
    }

    public ChatMessage(){

    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Map getTime() {
        return time;
    }

    public void setTime(long messageTime) {
        this.time = time;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String toString(){

        return this.messageUser + " sent " + messageText ;
    }
}