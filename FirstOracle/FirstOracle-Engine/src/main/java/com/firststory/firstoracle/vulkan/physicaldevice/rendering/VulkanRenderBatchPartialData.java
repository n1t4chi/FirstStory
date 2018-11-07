/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;

import java.util.List;

/**
 * @author n1t4chi
 */
class VulkanRenderBatchPartialData {
    
    private final VulkanGraphicCommandPool commandPool;
    private final VulkanGraphicPrimaryCommandBuffer primaryBuffer;
    private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers;
    private final VulkanPipeline trianglePipeline;
    private final VulkanPipeline linePipeline;
    
    VulkanRenderBatchPartialData(
        VulkanGraphicCommandPool commandPool,
        VulkanGraphicPrimaryCommandBuffer primaryBuffer,
        List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers,
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline
    ) {
        
        this.commandPool = commandPool;
        this.primaryBuffer = primaryBuffer;
        this.secondaryBuffers = secondaryBuffers;
        this.trianglePipeline = trianglePipeline;
        this.linePipeline = linePipeline;
    }
    
    void dispose() {
        primaryBuffer.dispose();
        secondaryBuffers.forEach( VulkanCommandBuffer::dispose );
        trianglePipeline.dispose();
        linePipeline.dispose();
        commandPool.dispose();
    }
    
    VulkanGraphicPrimaryCommandBuffer getPrimaryBuffer() {
        return primaryBuffer;
    }
}
