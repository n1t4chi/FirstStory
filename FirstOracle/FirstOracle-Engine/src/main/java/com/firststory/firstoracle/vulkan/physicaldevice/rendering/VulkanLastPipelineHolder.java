/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

/**
 * @author n1t4chi
 */
class VulkanLastPipelineHolder {
    
    private VulkanPipeline lastPipeline = null;
    
    VulkanPipeline getLastPipeline() {
        return lastPipeline;
    }
    
    void setLastPipeline( VulkanPipeline lastPipeline ) {
        this.lastPipeline = lastPipeline;
    }
}
