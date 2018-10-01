/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.buffer;

import java.io.IOException; /**
 * @author n1t4chi
 */
public class BufferException extends RuntimeException {
    
    public BufferException() {}
    
    public BufferException( IOException e ) {
        super( e );
    }
    
    public BufferException( String message ) {
        super( message );
    }
}
