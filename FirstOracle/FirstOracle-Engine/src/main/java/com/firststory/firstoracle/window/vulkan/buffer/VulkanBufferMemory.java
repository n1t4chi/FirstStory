/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.window.vulkan.*;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBufferException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotMapVulkanMemoryException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class VulkanBufferMemory extends LinearMemory< ByteBuffer > {
    
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
    
    public static boolean once = true;
    
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
    private VulkanBuffer copyBuffer = null;
    
    private VulkanBufferMemory(
        VulkanPhysicalDevice device,
        int byteLength,
        VulkanTransferCommandPool commandPool,
        int[] usageFlags,
        int[] memoryFlags
    )
    {
        this.device = device;
        this.byteLength = byteLength;
        this.commandPool = commandPool;
        memoryBuffer = new VulkanBuffer( usageFlags, memoryFlags, length() );
    }
    
    @Override
    public int length() {
        return byteLength;
    }
    
    @Override
    protected void writeUnsafe( LinearMemoryLocation location, ByteBuffer byteBuffer ) {
        location.setLength( byteBuffer.remaining() );
        
        if ( copyBuffer != null && copyBuffer.length < location.getLength() ) {
            copyBuffer.delete();
            copyBuffer = null;
        }
        
        if ( copyBuffer == null ) {
            copyBuffer = new VulkanBuffer( COPY_BUFFER_USAGE_FLAGS, COPY_BUFFER_MEMORY_FLAGS, location.getLength() );
        }
        
        copyMemory( byteBuffer, copyBuffer, location );
        copyBuffer.unmapMemory();
        copyBuffer.copyBuffer( memoryBuffer, commandPool, location );
        
        checkData( location );
        
    }
    
    @Override
    protected int getDataLength( ByteBuffer byteBuffer ) {
        return byteBuffer.remaining();
    }
    
    public VulkanAddress getAddress() {
        return memoryBuffer.bufferAddress;
    }
    
    public void close() {
        memoryBuffer.delete();
    }
    
    private void checkData( LinearMemoryLocation location ) {
        if ( once ) {
            return;
        }
        once = true;
    
        var location1 = new LinearMemoryLocation( location.getPosition(),
            location.getLength(),
            location.getTrueLength()
        );
        
        try {
            Thread.sleep( 5000 );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    
        var readGpuMemoryBuffer = new VulkanBuffer( new int[]{
            VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
        }, new int[]{
            VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
        }, location1.getLength() );
        
        memoryBuffer.copyBuffer( readGpuMemoryBuffer, commandPool, location1 );
    
        var readLocalMemory = MemoryUtil.memAlloc( ( int ) location1.getLength() );
        MemoryUtil.memCopy( readGpuMemoryBuffer.mapMemory().getValue(),
            MemoryUtil.memAddress( readLocalMemory ),
            location1.getLength()
        );
        readGpuMemoryBuffer.unmapMemory();
    
        var dst = new byte[ ( int ) location1.getLength() ];
        readLocalMemory.get( dst );
        System.out.println( " byte gpu: " + Arrays.toString( dst ) );
    
        var fdst = new float[ ( int ) location1.getLength() / 4 ];
        readLocalMemory.asFloatBuffer().get( fdst );
        
        System.out.println( " float gpu: " + Arrays.toString( fdst ) );
        
    }
    
    private void copyMemory( ByteBuffer dataBuffer, VulkanBuffer copyBuffer, LinearMemoryLocation location ) {
        MemoryUtil.memCopy( MemoryUtil.memAddress( dataBuffer ),
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
        )
        {
            commandPool.executeQueue( commandBuffer -> {
                var copyRegion = VkBufferCopy.create().srcOffset( 0 ) // Optional
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
            var memoryRequirements = VkMemoryRequirements.create();
            VK10.vkGetBufferMemoryRequirements( device.getLogicalDevice(),
                bufferAddress.getValue(),
                memoryRequirements
            );
            return memoryRequirements;
        }
        
        private VulkanAddress createBuffer( int[] usageFlags, long length ) {
            return VulkanHelper.createAddress( () -> {
                    var createInfo = VkBufferCreateInfo.create()
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
