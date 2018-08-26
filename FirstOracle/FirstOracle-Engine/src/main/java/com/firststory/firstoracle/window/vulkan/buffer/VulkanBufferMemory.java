/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.window.vulkan.*;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanMemoryExcpetion;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBufferException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotMapVulkanMemoryException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public class VulkanBufferMemory extends LinearMemory< ByteBuffer > {
    
    private static final int[] MEMORY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT,
        VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT
    };
    private static final int[] MEMORY_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT };
    private static final int[] COPY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT
    };
    private static final int[] COPY_BUFFER_MEMORY_FLAGS = {
        VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    private final VulkanPhysicalDevice device;
    private final int byteLength;
    private final VulkanTransferCommandPool commandPool;
    private final VulkanBuffer memoryBuffer;
    
    public VulkanBufferMemory( VulkanPhysicalDevice device, int byteLength, VulkanTransferCommandPool commandPool ) {
        this.device = device;
        this.byteLength = byteLength;
        this.commandPool = commandPool;
        memoryBuffer = new VulkanBuffer( MEMORY_BUFFER_USAGE_FLAGS, MEMORY_BUFFER_MEMORY_FLAGS, length() );
    }
    
    @Override
    public int length() {
        return byteLength;
    }
    
    public VulkanAddress getAddress() {
        return memoryBuffer.bufferAddress;
    }
    
    private VulkanBuffer copyBuffer = null;
    
    @Override
    protected void writeUnsafe( LinearMemoryLocation location, ByteBuffer byteBuffer ) {
        location.setLength( byteBuffer.remaining() );
        
        if( copyBuffer != null && copyBuffer.length < location.getLength() ) {
            copyBuffer.delete();
            copyBuffer = null;
        }
        
        if( copyBuffer == null ) {
            copyBuffer = new VulkanBuffer(
                COPY_BUFFER_USAGE_FLAGS,
                COPY_BUFFER_MEMORY_FLAGS,
                location.getLength()
            );
        }
        
        copyMemory( byteBuffer, copyBuffer, location );
        copyBuffer.unmapMemory();
        copyBuffer.copyBuffer( memoryBuffer, commandPool, location );
    }
    
    public void close() {
        memoryBuffer.delete();
    }
    
    @Override
    protected int getDataLength( ByteBuffer byteBuffer ) {
        return byteBuffer.remaining();
    }
    
    private void copyMemory( ByteBuffer dataBuffer, VulkanBuffer copyBuffer, LinearMemoryLocation location ) {
        MemoryUtil.memCopy(
            MemoryUtil.memAddress( dataBuffer ),
            copyBuffer.mapMemory().getValue(),
            location.getLength()
        );
//        MemoryUtil.memFree( dataBuffer );
    }
    
    private class VulkanBuffer {
        
        private final VulkanAddress bufferAddress;
        private final VkMemoryRequirements memoryRequirements;
        private final VulkanMemoryType memoryType;
        private final VkMemoryAllocateInfo memoryAllocateInfo;
        private final VulkanAddress allocatedMemoryAddress;
        private final long length;
        
        private VulkanBuffer( int[] bufferUsageFlags, int[] bufferMemoryFlags, long length ) {
            this.length = length;
            bufferAddress = createBuffer( bufferUsageFlags, length );
            memoryRequirements = createMemoryRequirements( bufferAddress );
            memoryType = selectRequiredMemoryType( bufferMemoryFlags );
            memoryAllocateInfo = createMemoryAllocateInfo( memoryRequirements, memoryType );
            allocatedMemoryAddress = allocateMemory( memoryAllocateInfo );
            bindMemoryToBuffer( bufferAddress, allocatedMemoryAddress );
        }
        
        private void copyBuffer(
            VulkanBuffer dstBuffer, VulkanTransferCommandPool commandPool, LinearMemoryLocation location
        ) {
            commandPool.executeQueue( commandBuffer -> {
                VkBufferCopy copyRegion = VkBufferCopy.create()
                    .srcOffset( 0 ) // Optional
                    .dstOffset( location.getPosition() ) // Optional
                    .size( location.getLength() );
                
                VK10.vkCmdCopyBuffer( commandBuffer.getCommandBuffer(),
                    this.bufferAddress.getValue(),
                    dstBuffer.bufferAddress.getValue(),
                    VkBufferCopy.create( 1 ).put( 0, copyRegion )
                );
            } );
        }
        
        private VulkanAddress mapMemory() {
            return VulkanHelper.createAddressViaBuffer( address -> VK10.vkMapMemory( device.getLogicalDevice(),
                allocatedMemoryAddress.getValue(),
                0,
                memoryAllocateInfo.allocationSize(),
                0,
                address
            ), resultCode -> new CannotMapVulkanMemoryException( device, resultCode ) );
        }
        
        private void unmapMemory() {
            VK10.vkUnmapMemory( device.getLogicalDevice(), allocatedMemoryAddress.getValue() );
        }
        
        private void delete() {
            if ( !bufferAddress.isNull() ) {
                VK10.vkDestroyBuffer( device.getLogicalDevice(), bufferAddress.getValue(), null );
                bufferAddress.setNull();
            }
            if ( !allocatedMemoryAddress.isNull() ) {
                VK10.vkFreeMemory( device.getLogicalDevice(), allocatedMemoryAddress.getValue(), null );
                allocatedMemoryAddress.setNull();
            }
        }
        
        private void bindMemoryToBuffer( VulkanAddress bufferAddress, VulkanAddress allocatedMemoryAddress ) {
            VulkanHelper.assertCall(
                () -> VK10.vkBindBufferMemory( device.getLogicalDevice(),
                    bufferAddress.getValue(),
                    allocatedMemoryAddress.getValue(),
                    0
                ),
                resultCode -> new CannotBindVulkanMemoryException( device, resultCode )
            );
        }
        
        private VulkanAddress allocateMemory( VkMemoryAllocateInfo allocateInfo ) {
            return VulkanHelper.createAddress( address -> VK10.vkAllocateMemory( device.getLogicalDevice(),
                allocateInfo,
                null,
                address
                ),
                resultCode -> new CannotAllocateVulkanMemoryExcpetion( device, resultCode )
            );
        }
        
        private VkMemoryAllocateInfo createMemoryAllocateInfo(
            VkMemoryRequirements memoryRequirements, VulkanMemoryType type
        )
        {
            return VkMemoryAllocateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
                .pNext( VK10.VK_NULL_HANDLE )
                .allocationSize( memoryRequirements.size() )
                .memoryTypeIndex( type.getIndex() );
        }
        
        private VulkanMemoryType selectRequiredMemoryType( int[] requiredMemoryTypeFlags ) {
            return device.selectMemoryType( memoryRequirements.memoryTypeBits(), requiredMemoryTypeFlags );
        }
        
        private VkMemoryRequirements createMemoryRequirements( VulkanAddress bufferAddress ) {
            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
            VK10.vkGetBufferMemoryRequirements( device.getLogicalDevice(),
                bufferAddress.getValue(),
                memoryRequirements
            );
            return memoryRequirements;
        }
        
        private VulkanAddress createBuffer( int[] usageFlags, long length ) {
            return VulkanHelper.createAddress( () -> {
                    VkBufferCreateInfo createInfo = VkBufferCreateInfo.create()
                        .sType( VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO )
                        .pNext( VK10.VK_NULL_HANDLE )
                        .size( length )
                        .usage( VulkanHelper.flagsToInt( usageFlags ) )
                        .flags( 0 );
                    if ( device.isSingleQueueFamilyUsed() ) {
                        createInfo.sharingMode( VK10.VK_SHARING_MODE_EXCLUSIVE );
                    } else {
                        createInfo.sharingMode( VK10.VK_SHARING_MODE_CONCURRENT )
                            .pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() );
                    }
                    return createInfo;
                },
                ( createInfo, address ) -> VK10.vkCreateBuffer( device.getLogicalDevice(), createInfo, null, address ),
                resultCode -> new CannotCreateVulkanVertexBufferException( device, resultCode )
            );
        }
    }
}
