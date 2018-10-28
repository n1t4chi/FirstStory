/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSemaphore;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanSubmitInfo {
    
    static VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            semaphoresListToBuffer( waitSemaphores ),
            MemoryUtil.memAllocPointer( 1 ).put( 0, commandBuffer.getAddress().getValue() ),
            MemoryUtil.memAllocLong( 1 ).put( 0, signalSemaphore.getAddress().getValue() )
        );
    }
    
    static VkSubmitInfo createSubmitInfo(
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            semaphoresListToBuffer( waitSemaphores ),
            null,
            MemoryUtil.memAllocLong( 1 ).put( 0, signalSemaphore.getAddress().getValue() )
        );
    }
    
    private static LongBuffer semaphoresListToBuffer( List< VulkanSemaphore > waitSemaphores ) {
        var semaphoresBuffer = MemoryUtil.memAllocLong( waitSemaphores.size() );
        for ( var i = 0; i < waitSemaphores.size(); i++ ) {
            semaphoresBuffer.put( i,
                waitSemaphores
                    .get( i )
                    .getAddress()
                    .getValue()
            );
        }
        return semaphoresBuffer;
    }
    
    static VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        VulkanSemaphore waitSemaphore,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            MemoryUtil.memAllocLong( 1 ).put( 0, waitSemaphore.getAddress().getValue() ),
            MemoryUtil.memAllocPointer( 1 ).put( 0, commandBuffer.getAddress().getValue() ),
            MemoryUtil.memAllocLong( 1 ).put( 0, signalSemaphore.getAddress().getValue() )
        );
    }
    
    private static VkSubmitInfo createSubmitInfo(
        LongBuffer semaphoresBuffer,
        PointerBuffer commandBuffers,
        LongBuffer signalSemaphores
    ) {
        return VkSubmitInfo
            .create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( commandBuffers )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( semaphoresBuffer )
            .pSignalSemaphores( signalSemaphores );
    }
    
    
    private static IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
}
