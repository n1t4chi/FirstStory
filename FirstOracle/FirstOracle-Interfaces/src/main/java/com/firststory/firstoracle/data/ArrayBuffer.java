/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public interface ArrayBuffer extends DataBuffer<float[]> {
    
    @Override
    boolean isLoaded();
    
    @Override
    boolean isCreated();
    
    @Override
    void create() throws CannotCreateBufferException;
    
    @Override
    void bind() throws BufferNotCreatedException, BufferNotLoadedException;
    
    @Override
    void load( float[] bufferData ) throws BufferNotCreatedException;
    
    @Override
    void delete() throws BufferNotCreatedException;
}
