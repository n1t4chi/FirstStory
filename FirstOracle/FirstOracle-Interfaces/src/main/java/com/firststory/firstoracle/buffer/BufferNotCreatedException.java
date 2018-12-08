/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.buffer;

/**
 * @author n1t4chi
 */
public class BufferNotCreatedException extends BufferException {
    
    public BufferNotCreatedException() {}
    
    public BufferNotCreatedException( Exception e ) {
        super( e );
    }
    
    public BufferNotCreatedException( String s ) {
        super( s );
    }
}
