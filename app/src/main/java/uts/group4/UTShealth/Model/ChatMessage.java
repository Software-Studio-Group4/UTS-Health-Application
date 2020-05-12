package uts.group4.UTShealth.Model;

import android.os.Message;

import java.util.Date;
import java.util.Map;

public class ChatMessage {

    private String id;
    private String text;
    private String name;
    private String imageUrl;

    public ChatMessage() {
    }

    public ChatMessage(String text, String name, String imageUrl) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean hasImageUrl(){
        return imageUrl == null;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}