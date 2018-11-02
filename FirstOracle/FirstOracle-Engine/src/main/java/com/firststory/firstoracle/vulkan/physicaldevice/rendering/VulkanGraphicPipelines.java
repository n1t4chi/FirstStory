/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanPipelineAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanPipelineLayoutException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDepthResources;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanGraphicPipelines {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    
    private final VulkanAddress pipelineLayout = VulkanAddress.createNull();
    
    private final VulkanPipelineAllocator firstRenderPipelineAllocator;
    private final VulkanPipelineAllocator firstStageRenderPipelineAllocator;
    private final VulkanPipelineAllocator continuingRenderPipelineAllocator;
    private final VulkanRenderPass exampleRenderPass;
    
    public VulkanGraphicPipelines( VulkanDeviceAllocator allocator, VulkanPhysicalDevice device, int topologyType ) {
        this.allocator = allocator;
        this.device = device;
        firstRenderPipelineAllocator = allocator.createPipelineAllocator(
            pipelineLayout,
            topologyType,
            true,
            false
        );
        firstStageRenderPipelineAllocator = allocator.createPipelineAllocator(
            pipelineLayout,
            topologyType,
            false,
            false
        );
        continuingRenderPipelineAllocator = allocator.createPipelineAllocator(
            pipelineLayout,
            topologyType,
            false,
            true
        );
        exampleRenderPass = new VulkanRenderPass( device );
    }
    
    public void dispose() {
        allocator.deregisterGraphicPipelines( this );
    }
    
    public void disposeUnsafe() {
        firstRenderPipelineAllocator.dispose();
        firstStageRenderPipelineAllocator.dispose();
        exampleRenderPass.dispose();
        clearLayout();
    }
    
    public void update(
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages,
        VulkanDepthResources depthResources,
        VulkanAddress descriptorSet
    ) {
        clearLayout();
        updateVulkanPipelineLayout( descriptorSet );
        
        firstStageRenderPipelineAllocator.update( swapChain, shaderStages, depthResources );
        firstRenderPipelineAllocator.update( swapChain, shaderStages, depthResources );
        continuingRenderPipelineAllocator.update( swapChain, shaderStages, depthResources );
        exampleRenderPass.updateRenderPass( swapChain, depthResources, false, false );
    }
    
    VulkanPipeline getFirstRenderPipeline() {
        return firstRenderPipelineAllocator.createPipeline();
    }
    
    VulkanPipeline getFirstStageRenderPipeline() {
        return firstStageRenderPipelineAllocator.createPipeline();
    }
    
    VulkanPipeline getContinuingRenderPipeline() {
        return continuingRenderPipelineAllocator.createPipeline();
    }
    
    public VulkanRenderPass getRenderPass() {
        return exampleRenderPass;
    }
    
    VulkanAddress getPipelineLayout() {
        return pipelineLayout;
    }
    
    private void clearLayout() {
        if( pipelineLayout.isNotNull() ) {
            VK10.vkDestroyPipelineLayout( device.getLogicalDevice(), pipelineLayout.getValue(), null );
            pipelineLayout.setNull();
        }
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
    
}
