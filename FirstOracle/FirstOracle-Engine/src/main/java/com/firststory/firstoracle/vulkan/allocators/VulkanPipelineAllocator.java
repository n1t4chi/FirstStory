/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDepthResources;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanPipeline;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanPipelineAllocator {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanAddress pipelineLayout;
    private final int topologyType;
    private final boolean pipelinesFirstUseOnly;
    
    private final VulkanReusableObjectsRegistry< VulkanPipeline > pipelines = new VulkanReusableObjectsRegistry<>( VulkanPipeline::disposeUnsafe );
    
    VulkanPipelineAllocator(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanAddress pipelineLayout,
        int topologyType,
        boolean pipelinesFirstUseOnly
    ) {
        this.allocator = allocator;
        this.device = device;
        this.pipelineLayout = pipelineLayout;
        this.topologyType = topologyType;
        this.pipelinesFirstUseOnly = pipelinesFirstUseOnly;
    }
    
    public VulkanPipeline createPipeline(
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages,
        VulkanDepthResources depthResources
    ) {
        return pipelines.register(
            () -> new VulkanPipeline( this, device, pipelineLayout, topologyType, pipelinesFirstUseOnly ),
            pipeline -> pipeline.update( swapChain, shaderStages, depthResources )
        );
    }
    
    public void deregisterPipeline( VulkanPipeline pipeline ) {
        pipelines.deregister( pipeline );
    }
    
    public void dispose() {
        allocator.deregisterPipelineAllocator( this );
    }
    
    void disposeUnsafe() {
        pipelines.dispose();
    }
}
