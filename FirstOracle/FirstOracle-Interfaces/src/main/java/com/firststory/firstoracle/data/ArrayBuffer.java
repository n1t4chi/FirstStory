/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public class ArrayBuffer implements DataBuffer<float[]> {
    
    private final ArrayBufferLoader loader;
    private int bufferID = 0;
    private boolean loaded = false;
    private int length;
    
    public ArrayBuffer( ArrayBufferLoader loader ) {
        this.loader = loader;
    }
    
    @Override
    public int getID() {
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
        assertCreated();
        loader.delete( bufferID );
        cleanUpFields();
    }
    
    private void cleanUpFields() {
        bufferID = 0;
        length = 0;
        loaded = false;
    }
}
