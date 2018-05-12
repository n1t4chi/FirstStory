/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanCommandBuffersException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanCommandPoolException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotSubmitVulkanDrawCommandBufferException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class VulkanCommandPool<CommandBuffer extends VulkanCommandBuffer> {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandPool.class );
    private final VulkanPhysicalDevice device;
    private final VulkanQueueFamily usedQueueFamily;
    private final Map< Integer, CommandBuffer > commandBuffers = new HashMap<>(  );
    private final VulkanSemaphore imageAvailableSemaphore;
    private VulkanAddress address;
    
    VulkanCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSemaphore imageAvailableSemaphore
    ) {
        this.device = device;
        this.usedQueueFamily = usedQueueFamily;
        this.imageAvailableSemaphore = imageAvailableSemaphore;
        address = createCommandPool();
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
    
    VulkanQueueFamily getUsedQueueFamily() {
        return usedQueueFamily;
    }
    
    void dispose() {
        disposeCommandBuffers();
        if( !address.isNull() ){
            VK10.vkDestroyCommandPool( device.getLogicalDevice(), address.getValue(), null );
            address.setNull();
        }
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    private CommandBuffer getCommandBuffer( int index ) {
        return commandBuffers.get( index );
    }
    
    void refreshCommandBuffers(
        Map< Integer, VulkanFrameBuffer > frameBuffers
    ) {
        disposeCommandBuffers();
        VulkanHelper.iterate( createCommandBufferBuffer( createAllocateInfo( frameBuffers ), frameBuffers.size() ),
            ( index, address ) -> commandBuffers.put( index,
                createNewCommandBuffer( index, new VulkanAddress( address ) , frameBuffers )
        ) );
    }
    
    abstract CommandBuffer createNewCommandBuffer(
        int index,
        VulkanAddress address,
        Map< Integer, VulkanFrameBuffer > frameBuffers
    );
    
    void executeQueue( VulkanCommands<CommandBuffer> commands ) {
        CommandBuffer commandBuffer = getCommandBuffer( getDevice().aquireNextImageIndex() );
        commandBuffer.fillQueue( commands );
        submitQueue( commandBuffer );
        postExecute( commandBuffer );
    }
    
    abstract void postExecute( CommandBuffer commandBuffer );
    
    VkSubmitInfo createSubmitInfo( VulkanCommandBuffer currentCommandBuffer ) {
        return VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .waitSemaphoreCount( 1 )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, imageAvailableSemaphore.getAddress().getValue() ) )
            .pCommandBuffers( MemoryUtil.memAllocPointer( 1 ).put(
                    0, currentCommandBuffer.getAddress().getValue() ) );
    }
    
    abstract IntBuffer createWaitStageMaskBuffer();
    
    private void submitQueue( CommandBuffer currentCommandBuffer ) {
        VkSubmitInfo submitInfo = createSubmitInfo( currentCommandBuffer ) ;
        
        VulkanHelper.assertCallOrThrow(
            ()-> VK10.vkQueueSubmit( usedQueueFamily.getQueue(), submitInfo, VK10.VK_NULL_HANDLE ),
            resultCode -> new CannotSubmitVulkanDrawCommandBufferException( this, resultCode, usedQueueFamily.getQueue() )
        );
    }
    
    private PointerBuffer createCommandBufferBuffer(
        VkCommandBufferAllocateInfo allocateInfo, int size
    ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( size );
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), allocateInfo, commandBuffersBuffer ),
            resultCode -> new CannotAllocateVulkanCommandBuffersException( device, resultCode )
        );
        return commandBuffersBuffer;
    }
    
    private void disposeCommandBuffers() {
        commandBuffers.forEach( ( integer, vulkanCommandBuffer ) -> vulkanCommandBuffer.close() );
        commandBuffers.clear();
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address.getValue() )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandBufferCount( frameBuffers.size() );
    }
    
    private VulkanAddress createCommandPool() {
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateCommandPool(
                device.getLogicalDevice(), createCommandPoolCreateInfo(), null, address ),
            resultCode -> new CannotCreateVulkanCommandPoolException( device, resultCode )
        );
    }
    
    private VkCommandPoolCreateInfo createCommandPoolCreateInfo() {
        return VkCommandPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO )
            .queueFamilyIndex( usedQueueFamily.getIndex() )
            .flags(
                //0
                //todo: when rendering is complete maybe they can raise performance:
                VK10.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
            );
    }
    
}
