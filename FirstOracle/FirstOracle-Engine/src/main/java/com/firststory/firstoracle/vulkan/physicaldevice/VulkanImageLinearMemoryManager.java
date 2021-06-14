/*
 * Copyright (c) 2021 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.util.HashMap;
import java.util.Map;

public class VulkanImageLinearMemoryManager {
    private final Map<VulkanMemoryType, VulkanImageLinearMemory> memoryAddressByType = new HashMap<>();
    private final VulkanPhysicalDevice device;
    
    public VulkanImageLinearMemoryManager( VulkanPhysicalDevice device ) {
        this.device = device;
    }
    
    public VulkanImageLinearMemory bindImageMemory(
        VulkanAddress image,
        VkMemoryRequirements memoryRequirements,
        VulkanMemoryType memoryType
    ) {
        var memory = getMemory( memoryType );
        memory.bind( image, memoryRequirements );
        return memory;
    }
    
    private VulkanImageLinearMemory getMemory( VulkanMemoryType memoryType ) {
        return memoryAddressByType.computeIfAbsent( memoryType, this::newLinearMemory );
    }
    
    private VulkanImageLinearMemory newLinearMemory( VulkanMemoryType memoryType ) {
        return new VulkanImageLinearMemory( device, memoryType );
    }
    
    public void disposeUnsafe() {
        memoryAddressByType.values().forEach( VulkanImageLinearMemory::disposeUnsafe );
        memoryAddressByType.clear();
    }
}
