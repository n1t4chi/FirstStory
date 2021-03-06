/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.buffer;

/**
 * @author n1t4chi
 */
public interface BufferProvider< Context, Data > {
    
    <Type> Context create( Data data ) throws CannotCreateBufferException;
}
