/*
 * Copyright (c) 2021 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanRegistry;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.util.HashMap;
import java.util.Map;

public class VulkanImageLinearMemoryManager implements VulkanRegistry {
    private final Map<VulkanMemoryType, VulkanImageLinearMemory> memoryAddressByType = new HashMap<>();
    
    public VulkanImageLinearMemory bindImageMemory(
        VulkanPhysicalDevice device,
        VulkanAddress image,
        VkMemoryRequirements memoryRequirements,
        VulkanMemoryType memoryType
    ) {
        var memory = getMemory( device, memoryType );
        memory.bind( image, memoryRequirements );
        return memory;
    }
    
    private VulkanImageLinearMemory getMemory(
        VulkanPhysicalDevice device,
        VulkanMemoryType memoryType
    ) {
        return memoryAddressByType.computeIfAbsent( memoryType, mt -> newLinearMemory( device, mt ) );
    }
    
    private VulkanImageLinearMemory newLinearMemory(
        VulkanPhysicalDevice device,
        VulkanMemoryType memoryType
    ) {
        return new VulkanImageLinearMemory( device, memoryType );
    }
    
    @Override
    public void dispose() {
        memoryAddressByType.values().forEach( VulkanImageLinearMemory::disposeUnsafe );
        memoryAddressByType.clear();
    }
}
