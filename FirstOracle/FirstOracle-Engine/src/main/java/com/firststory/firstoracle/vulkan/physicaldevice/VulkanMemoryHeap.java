/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import org.lwjgl.vulkan.VkMemoryHeap;

/**
 * @author n1t4chi
 */
class VulkanMemoryHeap {
    
    private final Integer index;
    private final VkMemoryHeap memoryHeap;
    
    VulkanMemoryHeap( Integer index, VkMemoryHeap memoryHeap ) {
        
        this.index = index;
        this.memoryHeap = memoryHeap;
    }
}
