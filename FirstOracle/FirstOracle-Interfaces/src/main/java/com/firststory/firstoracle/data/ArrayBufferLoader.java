/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public interface ArrayBufferLoader extends AutoCloseable {
    
    int create() throws CannotCreateBufferException;
    
    void bind( int bufferID );
    
    void load( int bufferID, float[] bufferData );
    
    void delete( int bufferID );
}
