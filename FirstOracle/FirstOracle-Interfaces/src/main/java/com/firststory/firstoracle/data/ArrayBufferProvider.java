/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public interface ArrayBufferProvider<Context extends DataBuffer< float[] > > extends BufferProvider<Context, float[] > {
    
    Context create( float[] data ) throws CannotCreateBufferException;
    
    void close();
}
