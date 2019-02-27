package com.firststory.firstslave;

import lombok.Data;

import java.util.Date;

@Data
public class ServerMessage {
    
    private String from;
    private String message;
    private String topic;
    private Date time = new Date();
    
    public ServerMessage() {}
    
    public ServerMessage( String from, String message, String topic ) {
        this.from = from;
        this.message = message;
        this.topic = topic;
    }
}
