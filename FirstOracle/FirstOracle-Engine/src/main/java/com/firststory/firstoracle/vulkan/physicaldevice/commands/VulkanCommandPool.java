/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.commands;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotAllocateVulkanCommandBuffersException;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotCreateVulkanCommandPoolException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.util.logging.Logger;

public abstract class VulkanCommandPool {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandPool.class );
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanQueueFamily usedQueueFamily;
    private final VulkanAddress address;
    
    public VulkanCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        this.allocator = allocator;
        this.device = device;
        this.usedQueueFamily = usedQueueFamily;
        address = createCommandPool();
    }
    
    public VulkanPhysicalDevice getDevice() {
        return device;
    }
    
    protected VulkanQueueFamily getUsedQueueFamily() {
        return usedQueueFamily;
    }
    
    public void dispose() {
        dispose( this.allocator );
    }
    
    protected abstract void dispose( VulkanDeviceAllocator allocator );
    
    public void disposeUnsafe() {
        if( address.isNotNull() ){
            VK10.vkDestroyCommandPool( device.getLogicalDevice(), address.getValue(), null );
            address.setNull();
        }
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    protected PointerBuffer createPrimaryCommandBufferBuffer( int size ) {
        return createCommandBufferBuffer( size, VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY );
    }
    
    protected PointerBuffer createSecondaryCommandBufferBuffer( int size ) {
        return createCommandBufferBuffer( size, VK10.VK_COMMAND_BUFFER_LEVEL_SECONDARY );
    }
    
    private PointerBuffer createCommandBufferBuffer( int size, int bufferLevel ) {
        var commandBuffersBuffer = MemoryUtil.memAllocPointer( size );
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(),
                createAllocateInfo( size, bufferLevel ),
                commandBuffersBuffer
            ),
            resultCode -> new CannotAllocateVulkanCommandBuffersException( device, resultCode )
        );
        return commandBuffersBuffer;
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( int size, int bufferLevel ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address.getValue() )
            .level( bufferLevel )
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
