/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.transfer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandBuffer extends VulkanCommandBuffer {
    
    VulkanTransferCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanCommandPool commandPool,
        int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, usedBeginInfoFlags );
    }
    
}