/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
class VulkanRenderBatchData {
    
    private final VulkanDescriptorPool descriptorPool;
    private final List< VulkanDataBuffer > uniformBuffers;
    private final List< VulkanRenderBatchPartialData > partialDatas;
    private final HashMap< Texture, VulkanDescriptorSet > setsByTexture;
    
    VulkanRenderBatchData(
        VulkanDescriptorPool descriptorPool,
        List< VulkanDataBuffer > uniformBuffers,
        List< VulkanRenderBatchPartialData > partialDatas,
        HashMap< Texture, VulkanDescriptorSet > setsByTexture
    ) {
        this.descriptorPool = descriptorPool;
        this.uniformBuffers = uniformBuffers;
        this.partialDatas = partialDatas;
        this.setsByTexture = setsByTexture;
    }
    
    void dispose() {
        descriptorPool.dispose();
        uniformBuffers.forEach( VulkanDataBuffer::dispose );
        partialDatas.forEach( VulkanRenderBatchPartialData::dispose );
        setsByTexture.values().forEach( VulkanDescriptorSet::dispose );
    }
    
    List< VulkanGraphicPrimaryCommandBuffer > getPrimaryBuffers() {
        return partialDatas.stream()
            .map( VulkanRenderBatchPartialData::getPrimaryBuffer )
            .collect( Collectors.toList())
        ;
    }
}
