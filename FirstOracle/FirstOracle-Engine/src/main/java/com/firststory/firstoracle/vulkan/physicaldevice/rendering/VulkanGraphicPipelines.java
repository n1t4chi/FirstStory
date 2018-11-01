/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanPipelineLayoutException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDepthResources;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.util.Arrays;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanGraphicPipelines {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    
    private final VulkanAddress pipelineLayout = VulkanAddress.createNull();
    
    private final VulkanPipeline backgroundPipeline;
    private final VulkanPipeline overlayPipeline;
    private final VulkanPipeline scene2DPipeline;
    private final VulkanPipeline scene3DPipeline;
    private final List< VulkanPipeline > pipelines;
    
    public VulkanGraphicPipelines( VulkanDeviceAllocator allocator, VulkanPhysicalDevice device, int topologyType ) {
        this.allocator = allocator;
        this.device = device;
        backgroundPipeline = new VulkanPipeline( device, pipelineLayout, topologyType, true );
        overlayPipeline = new VulkanPipeline( device, pipelineLayout, topologyType, false );
        scene2DPipeline = new VulkanPipeline( device, pipelineLayout, topologyType, false );
        scene3DPipeline = new VulkanPipeline( device, pipelineLayout, topologyType, false );
        pipelines = Arrays.asList(
            backgroundPipeline,
            overlayPipeline,
            scene2DPipeline,
            scene3DPipeline
        );
    }
    
    public void dispose() {
        allocator.deregisterGraphicPipelines( this );
    }
    
    public void disposeUnsafe() {
        pipelines.forEach( VulkanPipeline::dispose );
        
        if( pipelineLayout.isNotNull() ) {
            VK10.vkDestroyPipelineLayout( device.getLogicalDevice(), pipelineLayout.getValue(), null );
            pipelineLayout.setNull();
        }
    }
    
    public void update(
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages,
        VulkanDepthResources depthResources,
        VulkanAddress descriptorSet
    ) {
        disposeUnsafe();
        updateVulkanPipelineLayout( descriptorSet );
        pipelines.forEach( pipeline ->  pipeline.update( swapChain, shaderStages, depthResources ) );
    }
    
    private void updateVulkanPipelineLayout( VulkanAddress descriptorSet ) {
        var createInfo = VkPipelineLayoutCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO )
            .pSetLayouts( MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getValue() ) )
            .pPushConstantRanges( null );
        
        VulkanHelper.updateAddress( pipelineLayout,
            (address) -> VK10.vkCreatePipelineLayout( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanPipelineLayoutException( device, resultCode )
        );
    }
    
    VulkanPipeline getBackgroundPipeline() {
        return backgroundPipeline;
    }
    
    VulkanPipeline getScene2DPipeline() {
        return scene2DPipeline;
    }
    
    VulkanPipeline getScene3DPipeline() {
        return scene3DPipeline;
    }
    
    public VulkanPipeline getOverlayPipeline() {
        return overlayPipeline;
    }
    
    VulkanAddress getPipelineLayout() {
        return pipelineLayout;
    }
    
}
