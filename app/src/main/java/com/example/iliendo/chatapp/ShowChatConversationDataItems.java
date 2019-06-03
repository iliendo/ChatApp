package com.example.iliendo.chatapp;

/**
 * Created by iliendo on 6/2/19.
 */

public class ShowChatConversationDataItems {
    private String incommingMessage;
    private String outgoingMessage;

    public ShowChatConversationDataItems(){

    }

    public ShowChatConversationDataItems(String incommingMessage, String outgoingMessage) {
        this.incommingMessage = incommingMessage;
        this.outgoingMessage = outgoingMessage;
    }

    public String getIncommingMessage() {
        return incommingMessage;
    }

    public void setIncommingMessage(String incommingMessage) {
        this.incommingMessage = incommingMessage;
    }

    public String getOutgoingMessage() {
        return outgoingMessage;
    }

    public void setOutgoingMessage(String outgoingMessage) {
        this.outgoingMessage = outgoingMessage;
    }
}
