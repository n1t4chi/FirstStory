/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public interface BufferProvider< Context > {
    
    Context create() throws CannotCreateBufferException;
}
