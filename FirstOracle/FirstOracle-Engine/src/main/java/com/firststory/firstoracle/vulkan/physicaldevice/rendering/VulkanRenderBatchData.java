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
    
    VulkanRenderBatchData(
        VulkanGraphicCommandPool commandPool,
        VulkanGraphicPrimaryCommandBuffer primaryBuffer,
        List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers,
        ArrayList< VulkanDescriptorPool > descriptorsPools
    ) {
        this.commandPool = commandPool;
        this.primaryBuffer = primaryBuffer;
        this.secondaryBuffers = secondaryBuffers;
        this.descriptorsPools = descriptorsPools;
    }
    
    void dispose() {
        primaryBuffer.dispose();
        
        secondaryBuffers.forEach( VulkanCommandBuffer::dispose );
        secondaryBuffers.clear();
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        descriptorsPools.clear();
        
        commandPool.dispose();
    }
    
    VulkanGraphicPrimaryCommandBuffer getPrimaryBuffer() {
        return primaryBuffer;
    }
}
