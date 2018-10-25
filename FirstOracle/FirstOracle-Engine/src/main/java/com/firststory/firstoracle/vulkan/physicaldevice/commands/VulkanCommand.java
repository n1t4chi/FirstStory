/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.commands;

/**
 * @author n1t4chi
 */
public interface VulkanCommand<CommandBuffer extends VulkanCommandBuffer> {
    
    void execute( CommandBuffer commandBuffer );
}
