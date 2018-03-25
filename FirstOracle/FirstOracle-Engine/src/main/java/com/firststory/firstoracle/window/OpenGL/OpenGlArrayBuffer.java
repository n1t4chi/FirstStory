/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import com.firststory.firstoracle.data.ArrayBuffer;
import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;

/**
 * @author n1t4chi
 */
class OpenGlArrayBuffer implements ArrayBuffer {
    
    private final OpenGlArrayBufferLoader loader;
    private int bufferID = 0;
    private boolean loaded = false;
    private int length;
    
    public OpenGlArrayBuffer( OpenGlArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    @Override
    public int getBufferID() {
        return bufferID;
    }
    
    @Override
    public int getLength() {
        return length;
    }
    
    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public boolean isCreated() {
        return bufferID != 0;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {
        bufferID = loader.create();
    }
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        loader.bind( bufferID );
    }
    
    @Override
    public void load( float[] bufferData ) throws BufferNotCreatedException {
        assertCreated();
        loader.bind( bufferID );
        loader.load( bufferID, bufferData );
        length = bufferData.length;
        loaded = true;
    }
    
    @Override
    public void delete() throws BufferNotCreatedException {
        loader.delete( bufferID );
        cleanUpFields();
    }
    
    private void assertCreated() {
        if ( bufferID < 1 ) {
            throw new BufferNotCreatedException();
        }
    }
    
    private void assertLoaded() {
        if ( !loaded ) {
            throw new BufferNotLoadedException();
        }
    }
    
    private void cleanUpFields() {
        bufferID = 0;
        length = 0;
        loaded = false;
    }
}
