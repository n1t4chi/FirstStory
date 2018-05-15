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
    private VulkanAddress address;
    
    VulkanCommandPool( VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily ) {
        this.device = device;
        this.usedQueueFamily = usedQueueFamily;
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
    
    CommandBuffer getCommandBuffer( int index ) {
        return commandBuffers.get( index );
    }
    
    void refreshCommandBuffers(
        Map< Integer, VulkanFrameBuffer > frameBuffers
    ) {
        disposeCommandBuffers();
        VulkanHelper.iterate( createCommandBufferBuffer( frameBuffers.size() ),
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
        CommandBuffer commandBuffer = extractNextCommandBuffer();
        commandBuffer.fillQueue( commands );
        submitQueue( commandBuffer );
        postExecute( commandBuffer );
    }
    
    abstract CommandBuffer extractNextCommandBuffer();
    
    abstract void postExecute( CommandBuffer commandBuffer );
    
    VkSubmitInfo createSubmitInfo( VulkanCommandBuffer currentCommandBuffer ) {
        return VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
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
    
    PointerBuffer createCommandBufferBuffer( int size ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( size );
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), createAllocateInfo( size ), commandBuffersBuffer ),
            resultCode -> new CannotAllocateVulkanCommandBuffersException( device, resultCode )
        );
        return commandBuffersBuffer;
    }
    
    private void disposeCommandBuffers() {
        commandBuffers.forEach( ( integer, vulkanCommandBuffer ) -> vulkanCommandBuffer.close() );
        commandBuffers.clear();
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( int size ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address.getValue() )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandBufferCount( size );
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
