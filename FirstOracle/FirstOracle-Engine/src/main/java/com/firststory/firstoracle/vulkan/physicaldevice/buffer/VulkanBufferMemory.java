/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.exceptions.CannotAllocateVulkanMemoryException;
import com.firststory.firstoracle.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanVertexBufferException;
import com.firststory.firstoracle.vulkan.exceptions.CannotMapVulkanMemoryException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanMemoryType;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public class VulkanBufferMemory extends VulkanLinearMemory< ByteBuffer > {
    
    private static final int[] TEXTURE_MEMORY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
    };
    private static final int[] UNIFORM_MEMORY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT,
        VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
    };
    private static final int[] VERTEX_MEMORY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT,
        VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
        VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT
    };
    
    private static final int[] MEMORY_BUFFER_MEMORY_FLAGS = {
        VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
    };
    
    private static final int[] COPY_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT
    };
    
    private static final int[] COPY_BUFFER_MEMORY_FLAGS = {
        VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    static VulkanBufferMemory createUniformMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool dataTransferCommandPool
    ) {
        return create( device, byteLength, dataTransferCommandPool, UNIFORM_MEMORY_BUFFER_USAGE_FLAGS );
    }
    
    static VulkanBufferMemory createTextureMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool dataTransferCommandPool
    ) {
        return create( device, byteLength, dataTransferCommandPool, TEXTURE_MEMORY_BUFFER_USAGE_FLAGS );
    }
    
    static VulkanBufferMemory createVertexMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool dataTransferCommandPool
    ) {
        return create( device, byteLength, dataTransferCommandPool, VERTEX_MEMORY_BUFFER_USAGE_FLAGS );
    }
    
    static VulkanBufferMemory createVertexQuickMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool dataTransferCommandPool
    ) {
        return create( device, byteLength, dataTransferCommandPool, VERTEX_MEMORY_BUFFER_USAGE_FLAGS );
    }
    
    private static VulkanBufferMemory create(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool commandPool,
        int[] usageFlags
    ) {
        return new VulkanBufferMemory( device, byteLength, commandPool, usageFlags, MEMORY_BUFFER_MEMORY_FLAGS );
    }
    
    private final VulkanPhysicalDevice device;
    private final int byteLength;
    private final VulkanTransferCommandPool commandPool;
    private final VulkanBuffer memoryBuffer;
    private final VulkanBuffer localMemoryCopyBuffer;
    private final VulkanAddress mappedLocalMemory;
    
    private VulkanBufferMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool commandPool,
        int[] usageFlags,
        int[] memoryFlags
    ) {
        this.device = device;
        this.byteLength = byteLength;
        this.commandPool = commandPool;
        memoryBuffer = new VulkanBuffer( usageFlags, memoryFlags, length() );
        localMemoryCopyBuffer = new VulkanBuffer( COPY_BUFFER_USAGE_FLAGS, COPY_BUFFER_MEMORY_FLAGS, length() );
        mappedLocalMemory = new VulkanAddress();
    }
    
    @Override
    public int length() {
        return byteLength;
    }
    
    private void resetLocalTransferBuffers() {
        synchronized ( mappedLocalMemory ) {
            if ( mappedLocalMemory.isNotNull() ) {
                localMemoryCopyBuffer.unmapMemory();
                mappedLocalMemory.setNull();
            }
        }
    }
    
    public void dispose() {
        resetLocalTransferBuffers();
        memoryBuffer.delete();
        localMemoryCopyBuffer.delete();
    }
    
    @Override
    protected void writeUnsafe( VulkanLinearMemoryLocation location, ByteBuffer byteBuffer ) {
        synchronized ( mappedLocalMemory ) {
            location.setLength( byteBuffer.remaining() );
            if ( mappedLocalMemory.isNull() ) {
                localMemoryCopyBuffer.mapMemory();
            }
            copyMemory( byteBuffer, location );
            localMemoryCopyBuffer.copyBuffer( memoryBuffer, commandPool, location );
        }
    }
    
    @Override
    protected int getDataLength( ByteBuffer byteBuffer ) {
        return byteBuffer.remaining();
    }
    
    public VulkanAddress getAddress() {
        return memoryBuffer.bufferAddress;
    }
    
    private void copyMemory( ByteBuffer dataBuffer, VulkanLinearMemoryLocation location ) {
        MemoryUtil.memCopy(
            MemoryUtil.memAddress( dataBuffer ),
            mappedLocalMemory.getValue() + location.getPosition(),
            location.getLength()
        );
    }
    
    private class VulkanBuffer {
        
        private final VulkanAddress bufferAddress;
        private final VkMemoryRequirements memoryRequirements;
        private final VulkanMemoryType memoryType;
        private final VkMemoryAllocateInfo memoryAllocateInfo;
        private final VulkanAddress allocatedMemoryAddress;
        
        private VulkanBuffer( int[] bufferUsageFlags, int[] bufferMemoryFlags, long length ) {
            bufferAddress = createBuffer( bufferUsageFlags, length );
            memoryRequirements = createMemoryRequirements( bufferAddress );
            memoryType = selectRequiredMemoryType( bufferMemoryFlags );
            memoryAllocateInfo = createMemoryAllocateInfo( memoryRequirements, memoryType );
            allocatedMemoryAddress = allocateMemory( memoryAllocateInfo );
            bindMemoryToBuffer( bufferAddress, allocatedMemoryAddress );
        }
        
        private void copyBuffer(
            VulkanBuffer dstBuffer,
            VulkanTransferCommandPool commandPool,
            VulkanLinearMemoryLocation location
        ) {
            commandPool.addTransferData(
                this.bufferAddress,
                location.getPosition(),
                dstBuffer.bufferAddress,
                location.getPosition(),
                location.getLength()
            );
        }
        
        private void mapMemory() {
            VulkanHelper.updateAddressViaBuffer( mappedLocalMemory, address -> VK10.vkMapMemory( device.getLogicalDevice(),
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
            if ( bufferAddress.isNotNull() ) {
                VK10.vkDestroyBuffer( device.getLogicalDevice(), bufferAddress.getValue(), null );
                bufferAddress.setNull();
            }
            if ( allocatedMemoryAddress.isNotNull() ) {
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
                resultCode -> new CannotAllocateVulkanMemoryException( device, resultCode )
            );
        }
        
        private VkMemoryAllocateInfo createMemoryAllocateInfo(
            VkMemoryRequirements memoryRequirements,
            VulkanMemoryType type
        ) {
            return VkMemoryAllocateInfo.calloc()
                .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
                .pNext( VK10.VK_NULL_HANDLE )
                .allocationSize( memoryRequirements.size() )
                .memoryTypeIndex( type.getIndex() );
        }
        
        private VulkanMemoryType selectRequiredMemoryType( int[] requiredMemoryTypeFlags ) {
            return device.selectMemoryType( memoryRequirements.memoryTypeBits(), requiredMemoryTypeFlags );
        }
        
        private VkMemoryRequirements createMemoryRequirements( VulkanAddress bufferAddress ) {
            var memoryRequirements = VkMemoryRequirements.calloc();
            VK10.vkGetBufferMemoryRequirements( device.getLogicalDevice(),
                bufferAddress.getValue(),
                memoryRequirements
            );
            return memoryRequirements;
        }
        
        private VulkanAddress createBuffer( int[] usageFlags, long length ) {
            return VulkanHelper.createAddress( () -> {
                    var createInfo = VkBufferCreateInfo.calloc()
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
