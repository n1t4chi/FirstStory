/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import org.lwjgl.vulkan.VkMemoryType;

import java.util.Objects;

/**
 * @author n1t4chi
 */
public class VulkanMemoryType {
    
    private final Integer index;
    private final VkMemoryType memoryType;
    
    VulkanMemoryType( Integer index, VkMemoryType memoryType ) {
        
        this.index = index;
        this.memoryType = memoryType;
    }
    
    public Integer getIndex() {
        return index;
    }
    
    VkMemoryType getType() {
        return memoryType;
    }
    
    int heapIndex() {
        return memoryType.heapIndex();
    }
    
    int propertyFlags() {
        return memoryType.propertyFlags();
    }
    
    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        VulkanMemoryType that = ( VulkanMemoryType ) o;
        return Objects.equals( getIndex(), that.getIndex() ) && Objects.equals( memoryType, that.memoryType );
    }
    
    @Override public int hashCode() {
        return Objects.hash( getIndex(), memoryType );
    }
}
