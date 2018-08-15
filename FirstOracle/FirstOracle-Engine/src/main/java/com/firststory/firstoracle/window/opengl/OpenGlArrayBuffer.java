/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.data.DataBuffer;

/**
 * @author n1t4chi
 */
class OpenGlArrayBuffer implements DataBuffer< float[] > {
    
    private final OpenGlArrayBufferLoader loader;
    private int bufferID = -1;
    private boolean loaded = false;
    private int length;
    
    OpenGlArrayBuffer( OpenGlArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    int getBufferId() {
        return bufferID;
    }
    
    public int getLength() {
        return length;
    }
    
    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public boolean isCreated() {
        return bufferID > 0;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {
        bufferID = loader.createBuffer();
    }
    
    @Override
    public void bindUnsafe() throws BufferNotCreatedException, BufferNotLoadedException {
        loader.bind( bufferID );
    }
    
    @Override
    public void loadUnsafe( float[] bufferData ) throws BufferNotCreatedException {
        loader.bind( bufferID );
        loader.load( bufferID, bufferData );
        length = bufferData.length;
        loaded = true;
    }
    
    @Override
    public void deleteUnsafe() throws BufferNotCreatedException {
        loader.delete( bufferID );
        cleanUpFields();
    }
    
    private void cleanUpFields() {
        bufferID = 0;
        length = 0;
        loaded = false;
    }
}
