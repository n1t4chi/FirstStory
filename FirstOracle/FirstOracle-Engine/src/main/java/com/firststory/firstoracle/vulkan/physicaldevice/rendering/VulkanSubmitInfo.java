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
        List< VulkanGraphicPrimaryCommandBuffer > commandBuffers,
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            waitSemaphoreListToBuffer( waitSemaphores ),
            commandBufferListToBuffer( commandBuffers ),
            signalSemaphoreToBuffer( signalSemaphore )
        );
    }
    
    static VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            waitSemaphoreListToBuffer( waitSemaphores ),
            commandBufferToBuffer( commandBuffer ),
            signalSemaphoreToBuffer( signalSemaphore )
        );
    }
    
    static VkSubmitInfo createSubmitInfo(
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            waitSemaphoreListToBuffer( waitSemaphores ),
            null,
            signalSemaphoreToBuffer( signalSemaphore )
        );
    }
    
    static VkSubmitInfo createSubmitInfo(
        List< VulkanGraphicPrimaryCommandBuffer > commandBuffers,
        VulkanSemaphore waitSemaphore,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            waitSemaphoreToBuffer( waitSemaphore ),
            commandBufferListToBuffer( commandBuffers ),
            signalSemaphoreToBuffer( signalSemaphore )
        );
    }
    
    private static LongBuffer waitSemaphoreToBuffer( VulkanSemaphore waitSemaphore ) {
        return MemoryUtil
            .memAllocLong( 1 ).put( 0, waitSemaphore.getAddressForWait().getValue() );
    }
    
    static VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        VulkanSemaphore waitSemaphore,
        VulkanSemaphore signalSemaphore
    ) {
        return createSubmitInfo(
            waitSemaphoreToBuffer( waitSemaphore ),
            commandBufferToBuffer( commandBuffer ),
            signalSemaphoreToBuffer( signalSemaphore )
        );
    }
    
    private static PointerBuffer commandBufferToBuffer( VulkanGraphicPrimaryCommandBuffer commandBuffer ) {
        return MemoryUtil.memAllocPointer( 1 ).put( 0, commandBuffer.getAddress().getValue() );
    }
    
    private static LongBuffer signalSemaphoreToBuffer( VulkanSemaphore signalSemaphore ) {
        return MemoryUtil.memAllocLong( 1 ).put( 0, signalSemaphore.getAddressForSignal().getValue() );
    }
    
    private static VkSubmitInfo createSubmitInfo(
        LongBuffer waitSemaphoresBuffer,
        PointerBuffer commandBuffers,
        LongBuffer signalSemaphores
    ) {
        return VkSubmitInfo
            .create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( commandBuffers )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( waitSemaphoresBuffer )
            .pSignalSemaphores( signalSemaphores );
    }
    
    private static PointerBuffer commandBufferListToBuffer( List< VulkanGraphicPrimaryCommandBuffer > commandBuffers ) {
        if( commandBuffers.isEmpty() ) {
            return null;
        }
        var commandBuffersBuffer = MemoryUtil.memAllocPointer( commandBuffers.size() );
        for ( var i = 0; i < commandBuffers.size(); i++ ) {
            commandBuffersBuffer.put( i, commandBuffers.get( i ).getAddress().getValue() );
        }
        return commandBuffersBuffer;
    }
    
    private static LongBuffer waitSemaphoreListToBuffer( List< VulkanSemaphore > waitSemaphores ) {
        if( waitSemaphores.isEmpty() ) {
            return null;
        }
        var semaphoresBuffer = MemoryUtil.memAllocLong( waitSemaphores.size() );
        for ( var i = 0; i < waitSemaphores.size(); i++ ) {
            semaphoresBuffer.put( i, waitSemaphores.get( i ).getAddressForWait().getValue() );
        }
        return semaphoresBuffer;
    }
    
    private static IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
}
