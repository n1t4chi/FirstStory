/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
class VulkanRenderBatchData {
    
    private final VulkanDescriptorPool descriptorPool;
    private final List< VulkanDataBuffer > uniformBuffers;
    private final List< VulkanRenderBatchPartialData > partialDatas;
    
    VulkanRenderBatchData(
        VulkanDescriptorPool descriptorPool,
        List< VulkanDataBuffer > uniformBuffers,
        List< VulkanRenderBatchPartialData > partialDatas
    ) {
        this.descriptorPool = descriptorPool;
        this.uniformBuffers = uniformBuffers;
        this.partialDatas = partialDatas;
    }
    
    void dispose() {
        descriptorPool.dispose();
        uniformBuffers.forEach( VulkanDataBuffer::dispose );
        partialDatas.forEach( VulkanRenderBatchPartialData::dispose );
    }
    
    List< VulkanGraphicPrimaryCommandBuffer > getPrimaryBuffers() {
        return partialDatas.stream()
            .map( VulkanRenderBatchPartialData::getPrimaryBuffer )
            .collect( Collectors.toList())
        ;
    }
}
