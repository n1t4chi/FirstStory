/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
interface VulkanCommand<CommandBuffer extends VulkanCommandBuffer> {
    
    void execute( CommandBuffer commandBuffer );
}