/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.buffer.DataBuffer;

/**
 * @author n1t4chi
 */
class MockBuffer implements DataBuffer< float[]> {
    
    private final float[] bufferData;
    
    MockBuffer( float[] array ) {
        bufferData = array;
    }
    
    float[] getBufferData() {
        return bufferData;
    }
    
    @Override
    public boolean isLoaded() {
        return true;
    }
    
    @Override
    public boolean isCreated() {
        return true;
    }
    
    @Override
    public void create() {}
    
    @Override
    public void bindUnsafe() {}
    
    @Override
    public void loadUnsafe( float[] bufferData ) {}
    
    @Override
    public void deleteUnsafe() {}
}
