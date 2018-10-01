/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.buffer;

import java.io.IOException; /**
 * @author n1t4chi
 */
public class BufferNotCreatedException extends BufferException {
    
    public BufferNotCreatedException() {}
    
    public BufferNotCreatedException( IOException e ) {
        super( e );
    }
    
    public BufferNotCreatedException( String s ) {
        super( s );
    }
}
