/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanGraphicSecondaryCommandBuffer extends VulkanCommandBuffer< VulkanGraphicSecondaryCommandBuffer > {
    
    private VkCommandBufferInheritanceInfo inheritanceInfo;
    
    VulkanGraphicSecondaryCommandBuffer(
        VulkanCommandBufferAllocator< VulkanGraphicSecondaryCommandBuffer > allocator,
        VulkanPhysicalDevice device,
        VulkanGraphicCommandPool commandPool,
        VulkanAddress address,
        int... usedBeginInfoFlags
    ) {
        super(
            allocator,
            device,
            commandPool,
            address,
            usedBeginInfoFlags );
    }
    
    public void update(
        VulkanRenderPass renderPass,
        int subpassIndex,
        VulkanFrameBuffer frameBuffer
    ) {
        inheritanceInfo = VkCommandBufferInheritanceInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .subpass( subpassIndex )
            .framebuffer( frameBuffer.getAddress().getValue() );
    }
    
    @Override
    protected VkCommandBufferInheritanceInfo createInheritanceInfo() {
        return inheritanceInfo;
    }
    
    void setLineWidth( Float lineWidth ) {
        VK10.vkCmdSetLineWidth( getCommandBuffer(), lineWidth );
    }
    
    void draw(
        VulkanDataBuffer vertexBuffer,
        VulkanDataBuffer uvBuffer,
        VulkanDataBuffer colourBuffer,
        VulkanDataBuffer dataBuffer,
        int bufferSize
    ) {
        VK10.vkCmdBindVertexBuffers( getCommandBuffer(),
            0,
            new long[]{
                vertexBuffer.getBufferAddress().getValue(),
                uvBuffer.getBufferAddress().getValue(),
                colourBuffer.getBufferAddress().getValue(),
                dataBuffer.getBufferAddress().getValue()
            },
            new long[]{
                vertexBuffer.getMemoryOffset(),
                uvBuffer.getMemoryOffset(),
                colourBuffer.getMemoryOffset(),
                dataBuffer.getMemoryOffset(),
            }
        );
        VK10.vkCmdDraw(
            getCommandBuffer(),
            bufferSize,
            1,
            0,
            0
        );
    }
    
    void bindDescriptorSets( VulkanGraphicPipelines graphicPipelines, VulkanDescriptorSet descriptorSet ) {
        VK10.vkCmdBindDescriptorSets( getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipelines.getPipelineLayout().getValue(),
            0,
            MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getAddress().getValue() ),
            null
        );
    }
    
    void bindPipeline( VulkanPipeline graphicPipeline ) {
        VK10.vkCmdBindPipeline(
            getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getGraphicPipeline().getValue()
        );
    }
}
