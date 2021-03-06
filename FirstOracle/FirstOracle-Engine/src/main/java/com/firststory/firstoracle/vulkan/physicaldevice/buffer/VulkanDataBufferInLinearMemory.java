/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.buffer.BufferNotCreatedException;
import com.firststory.firstoracle.buffer.BufferNotLoadedException;
import com.firststory.firstoracle.buffer.CannotCreateBufferException;
import com.firststory.firstoracle.buffer.DataBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferInLinearMemory< Data > implements DataBuffer<Data> {
    
    private final VulkanLinearMemoryLocation location;
    private final VulkanLinearMemoryController< ?, Data > controller;
    private boolean usable = true;
    private boolean loaded = false;
    
    public VulkanDataBufferInLinearMemory(
        VulkanLinearMemoryController< ?, Data > controller,
        VulkanLinearMemoryLocation memoryLocation
    ) {
        this.controller = controller;
        this.location = memoryLocation;
    }
    
    public long length() {
        assertCreated();
        return location.getLength();
    }
    
    public VulkanLinearMemoryLocation getLocation() {
        return location;
    }
    
    public long getMemoryOffset() {
        return location.getPosition();
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
