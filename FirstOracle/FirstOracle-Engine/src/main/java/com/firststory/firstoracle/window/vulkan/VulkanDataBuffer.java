/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanMemoryExcpetion;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBufferException;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public abstract class VulkanDataBuffer< Data > implements DataBuffer< Data > {
    
    private final VulkanPhysicalDevice device;
    private final int[] usageFlags;
    private final int[] requiredMemoryTypeFlags;
    private VulkanAddress bufferAddress = VulkanAddress.createNull();
    private VkMemoryRequirements memoryRequirements;
    private VulkanMemoryType usedMemoryType;
    private VkMemoryAllocateInfo allocateInfo;
    private VulkanAddress allocatedMemoryAddress = VulkanAddress.createNull();
    private int dataCount;
    private int dataSize;
    
    VulkanDataBuffer(
        VulkanPhysicalDevice device,
        int[] usageFlags,
        int[] requiredMemoryTypeFlags
    ) {
        this.device = device;
        this.usageFlags = usageFlags;
        this.requiredMemoryTypeFlags = requiredMemoryTypeFlags;
    }
    
    @Override
    public boolean isLoaded() {
        return !bufferAddress.isNull();
    }
    
    @Override
    public boolean isCreated() {
        return true;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {}
    
//    @Override
//    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
//        assertCreated();
//        assertLoaded();
//        loader.bind( this );
//    }
    
    @Override
    public void delete() throws BufferNotCreatedException {
        close();
    }
    
    public void close() {
        if ( !bufferAddress.isNull() ) {
            VK10.vkDestroyBuffer( device.getLogicalDevice(), bufferAddress.getValue(), null );
            bufferAddress.setNull();
        }
        if ( !allocatedMemoryAddress.isNull() ) {
            VK10.vkFreeMemory( device.getLogicalDevice(), allocatedMemoryAddress.getValue(), null );
            allocatedMemoryAddress.setNull();
        }
    }
    
    public int getDataSizeInBytes() {
        return dataSize * dataCount;
    }
    
    public int getDataLengthInArray() {
        return dataCount;
    }
    
    void createBuffer( int dataLength, int dataSize ) {
        this.dataCount = dataLength;
        this.dataSize = dataSize;
//        vertexBuffer = createVertexDataBuffer();
        
        bufferAddress = createBuffer();
        memoryRequirements = createMemoryRequirements();
        usedMemoryType = selectRequiredMemoryType();
        
        allocateInfo = createMemoryAllocateInfo();
        allocatedMemoryAddress = allocateMemory();
        bindMemoryToBuffer();
    }
    
    VulkanAddress getBufferAddress() {
        return bufferAddress;
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
    
    VkDevice getDeviceLogicalDevice() {
        return device.getLogicalDevice();
    }
    
    VkMemoryAllocateInfo getAllocateInfo() {
        return allocateInfo;
    }
    
    VulkanAddress getAllocatedMemoryAddress() {
        return allocatedMemoryAddress;
    }
    
    private void bindMemoryToBuffer() {
        VulkanHelper.assertCall( () -> VK10.vkBindBufferMemory( device.getLogicalDevice(),
            bufferAddress.getValue(),
            allocatedMemoryAddress.getValue(),
            0
            ),
            resultCode -> new CannotBindVulkanMemoryException( device, resultCode )
        );
    }
    
    private VulkanAddress allocateMemory() {
        return VulkanHelper.createAddress( address -> VK10.vkAllocateMemory( device.getLogicalDevice(),
            allocateInfo,
            null,
            address
            ),
            resultCode -> new CannotAllocateVulkanMemoryExcpetion( device, resultCode )
        );
    }
    
    private VkMemoryAllocateInfo createMemoryAllocateInfo() {
        return VkMemoryAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .allocationSize( memoryRequirements.size() )
            .memoryTypeIndex( usedMemoryType.getIndex() );
    }
    
    private VulkanMemoryType selectRequiredMemoryType() {
        return device.selectMemoryType( memoryRequirements.memoryTypeBits(), requiredMemoryTypeFlags );
    }
    
    private VkMemoryRequirements createMemoryRequirements() {
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
        VK10.vkGetBufferMemoryRequirements( device.getLogicalDevice(), bufferAddress.getValue(), memoryRequirements );
        return memoryRequirements;
    }
    
    private VulkanAddress createBuffer() {
        return VulkanHelper.createAddress( () -> {
                VkBufferCreateInfo createInfo = VkBufferCreateInfo.create()
                    .sType( VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO )
                    .pNext( VK10.VK_NULL_HANDLE )
                    .size( getDataSizeInBytes() )
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
