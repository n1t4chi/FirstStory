/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.data;


/**
 * @author n1t4chi
 */
public interface DataBuffer<Data> extends AutoCloseable {
    
    int getID();
    
    boolean isLoaded();
    
    boolean isCreated();
    
    void create() throws CannotCreateBufferException;
    
    void bind() throws BufferNotCreatedException, BufferNotLoadedException;
    
    void load( Data bufferData ) throws BufferNotCreatedException;
    
    void delete() throws BufferNotCreatedException;
    
    @Override
    default void close() throws BufferNotCreatedException {
        delete();
    }
    
    default void assertCreated() {
        if ( !isCreated() ) {
            throw new BufferNotCreatedException();
        }
    }
    
    default void assertLoaded() {
        if ( !isLoaded() ) {
            throw new BufferNotLoadedException();
        }
    }
}
