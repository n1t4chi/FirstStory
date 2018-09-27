/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandBuffer extends VulkanCommandBuffer {
    
    public VulkanTransferCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanCommandPool< ? > commandPool,
        int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, usedBeginInfoFlags );
    }
}
