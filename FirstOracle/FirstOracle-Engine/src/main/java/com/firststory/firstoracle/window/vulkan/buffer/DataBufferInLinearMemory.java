/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.data.DataBuffer;

/**
 * @author n1t4chi
 */
public class DataBufferInLinearMemory< Data > implements DataBuffer<Data> {
    
    private final LinearMemoryLocation location;
    private final LinearMemoryController< Data > controller;
    private boolean usable = true;
    private boolean loaded = false;
    
    public DataBufferInLinearMemory( LinearMemoryController< Data > controller, LinearMemoryLocation memoryLocation ) {
        this.controller = controller;
        this.location = memoryLocation;
    }
    
    public int length() {
        assertCreated();
        return location.getLength();
    }
    
    public LinearMemoryLocation provideLocation() {
//        assertCreated();
//        assertLoaded();
        return location;
    }
    
    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public boolean isCreated() {
        return usable;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {}
    
    @Override
    public void bindUnsafe() throws BufferNotCreatedException, BufferNotLoadedException {}
    
    @Override
    public void loadUnsafe( Data bufferData ) throws BufferNotCreatedException {
        loaded = true;
        controller.write( location, bufferData );
    }
    
    @Override
    public void deleteUnsafe() throws BufferNotCreatedException {
        usable = false;
        loaded = false;
        controller.free( location );
    }
}
