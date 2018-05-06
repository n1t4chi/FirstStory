/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public interface ArrayBufferProvider<Context> extends AutoCloseable {
    
    Context create() throws CannotCreateBufferException;
    
    void bind( Context context );
    
    void load( Context context, float[] bufferData );
    
    void delete( Context context );
}
