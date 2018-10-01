/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.buffer;

/**
 * @author n1t4chi
 */
public interface DataBuffer< Data > {
    
    boolean isLoaded();
    
    boolean isCreated();
    
    void create() throws CannotCreateBufferException;
    
    void bindUnsafe() throws BufferNotCreatedException, BufferNotLoadedException;
    
    void loadUnsafe( Data bufferData ) throws BufferNotCreatedException;
    
    void deleteUnsafe() throws BufferNotCreatedException;
    
    default void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        bindUnsafe();
    }
    
    default void load( Data bufferData ) throws BufferNotCreatedException {
        assertCreated();
        loadUnsafe( bufferData );
    }
    
    default void delete() throws BufferNotCreatedException {
        assertCreated();
        deleteUnsafe();
    }
    
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
