/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public class ArrayBuffer<Context> implements DataBuffer<Context, float[]> {
    
    private final ArrayBufferProvider<Context> loader;
    private Context bufferID = null;
    private boolean loaded = false;
    private int length;
    
    public ArrayBuffer( ArrayBufferProvider<Context> loader ) {
        this.loader = loader;
    }
    
    @Override
    public Context getContext() {
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
        return bufferID != null;
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
        assertCreated();
        loader.delete( bufferID );
        cleanUpFields();
    }
    
    private void cleanUpFields() {
        bufferID = null;
        length = 0;
        loaded = false;
    }
}
