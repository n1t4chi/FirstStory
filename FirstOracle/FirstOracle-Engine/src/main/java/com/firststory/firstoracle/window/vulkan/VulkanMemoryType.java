/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VkMemoryType;

/**
 * @author n1t4chi
 */
class VulkanMemoryType {
    
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
}
