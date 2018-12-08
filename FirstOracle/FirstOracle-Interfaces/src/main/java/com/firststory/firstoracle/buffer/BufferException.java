/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.buffer;

/**
 * @author n1t4chi
 */
public class BufferException extends RuntimeException {
    
    public BufferException() {}
    
    public BufferException( Exception e ) {
        super( e );
    }
    
    public BufferException( String message ) {
        super( message );
    }
}
