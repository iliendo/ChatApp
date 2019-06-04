package com.example.iliendo.chatapp;

/**
 * Created by iliendo on 6/2/19.
 */

public class ShowChatConversationDataItems {
    private String message;
    private String sender;

    public ShowChatConversationDataItems() {
    }

    public ShowChatConversationDataItems(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
