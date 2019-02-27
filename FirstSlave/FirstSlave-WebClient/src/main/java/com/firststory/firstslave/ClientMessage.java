package com.firststory.firstslave;

import lombok.Data;

@Data
public class ClientMessage {
    
    private String from;
    private String text;
    
    public ClientMessage() {}
    
    public ClientMessage( String from, String text ) {
        this.from = from;
        this.text = text;
    }
}
