/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
class VulkanRenderBatchData {
    
    private final VulkanGraphicCommandPool commandPool;
    private final VulkanGraphicPrimaryCommandBuffer primaryBuffer;
    private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers;
    private final ArrayList< VulkanDescriptorPool > descriptorsPools;
    private final VulkanPipeline trianglePipeline;
    private final VulkanPipeline linePipeline;
    
    VulkanRenderBatchData(
        VulkanGraphicCommandPool commandPool,
        VulkanGraphicPrimaryCommandBuffer primaryBuffer,
        List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers,
        ArrayList< VulkanDescriptorPool > descriptorsPools,
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline
    ) {
        this.commandPool = commandPool;
        this.primaryBuffer = primaryBuffer;
        this.secondaryBuffers = secondaryBuffers;
        this.descriptorsPools = descriptorsPools;
        this.trianglePipeline = trianglePipeline;
        this.linePipeline = linePipeline;
    }
    
    void dispose() {
        primaryBuffer.dispose();
        
        secondaryBuffers.forEach( VulkanCommandBuffer::dispose );
        secondaryBuffers.clear();
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        descriptorsPools.clear();
        trianglePipeline.dispose();
        linePipeline.dispose();
        commandPool.dispose();
    }
    
    VulkanGraphicPrimaryCommandBuffer getPrimaryBuffer() {
        return primaryBuffer;
    }
}
