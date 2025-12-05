package com.example.elderlycare.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String messageId;
    private String senderId;
    private String text;
    private Timestamp timestamp;
    private boolean seen;

    public ChatMessage() {}

    public ChatMessage(String messageId, String senderId, String text, Timestamp timestamp, boolean seen) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    // Getters and setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
