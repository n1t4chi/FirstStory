package com.firststory.firstmaster;

import lombok.Data;

import java.util.Date;

@Data
public class OutputMessage {
    
    private String from;
    private String message;
    private String topic;
    private Date time = new Date();
    
    public OutputMessage() {}
    
    public OutputMessage( String from, String message, String topic ) {
        this.from = from;
        this.message = message;
        this.topic = topic;
    }
}
