package com.example.aplicatie;

import java.sql.Timestamp;

public class Message {

    private String content;
    private Timestamp timestamp;
    private int id;

    public Message(String content, Timestamp timestamp, int id) {
        this.content = content;
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
