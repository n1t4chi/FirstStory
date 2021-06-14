/*
 * Copyright (c) 2021 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

public class VulkanImageLinearMemoryLocation {
    private final VulkanImageLinearMemoryManager memory;
    private final int offset;
    private final int length;
    
    public VulkanImageLinearMemoryLocation( VulkanImageLinearMemoryManager memory, int offset, int length ) {
        this.memory = memory;
        this.offset = offset;
        this.length = length;
    }
}
