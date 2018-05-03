/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanMemoryExcpetion;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBuffer;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotMapVulkanMemoryException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBuffer {
    
    private static final int ATTRIBUTES = 2 + 3;
    private static final int VERTEX_DATA_SIZE = ATTRIBUTES * 4;
    
    
    private final VulkanPhysicalDevice device;
    private final int length;
    private final VulkanAddress bufferAddress;
    private final VkMemoryRequirements memoryRequirements;
    private final VulkanMemoryType usedMemoryType;
    private final ByteBuffer vertexBuffer;
    private final float[] data;
    private final VkMemoryAllocateInfo allocateInfo;
    private final VulkanAddress allocatedMemoryAddress;
    private final VulkanAddress mappedMemoryAddres;
    
    VulkanAddress getBufferAddress() {
        return bufferAddress;
    }
    
    VulkanDataBuffer( VulkanPhysicalDevice device, float[] dataArray ) {
        this.device = device;
        this.data = dataArray;
        length = caluclateLength();
        vertexBuffer = createVertexDataBuffer();

        bufferAddress = createBuffer();
        memoryRequirements = createMemoryRequirements();
        usedMemoryType = selectRequiredMemoryType();
    
        allocateInfo = createMemoryAllocateInfo();
        allocatedMemoryAddress = allocateMemory();
        mappedMemoryAddres = mapMemory();
        copyMemory();
        unmapMemory();
        bindMemoryToBuffer();
    }
    
    private void copyMemory() {
        MemoryUtil.memCopy(MemoryUtil.memAddress( vertexBuffer ), mappedMemoryAddres.getValue(), vertexBuffer.remaining());
        MemoryUtil.memFree( vertexBuffer );
    }
    
    private VulkanAddress mapMemory() {
        return VulkanHelper.createAddressViaBuffer(
            address -> VK10.vkMapMemory(device.getLogicalDevice(), allocatedMemoryAddress.getValue(),
                0, allocateInfo.allocationSize(), 0, address ),
            resultCode -> new CannotMapVulkanMemoryException( device, resultCode )
        );
    }
    private void unmapMemory() {
        VK10.vkUnmapMemory( device.getLogicalDevice(), allocatedMemoryAddress.getValue() );
    }
    
    private int caluclateLength() {
        return data.length / ATTRIBUTES;
    }
    
    private ByteBuffer createVertexDataBuffer() {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( length * VERTEX_DATA_SIZE );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
    }
    
    int getVertexDataSize() {
        return VERTEX_DATA_SIZE;
    }
    
    private void bindMemoryToBuffer() {
        VulkanHelper.assertCall(
            () -> VK10.vkBindBufferMemory(device.getLogicalDevice(), bufferAddress.getValue(),
                allocatedMemoryAddress.getValue(), 0 ),
            resultCode -> new CannotBindVulkanMemoryException( device, resultCode )
        );
    }

    private VulkanAddress allocateMemory() {
        return VulkanHelper.createAddress(
            address -> VK10.vkAllocateMemory( device.getLogicalDevice(),
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
        return device.selectMemoryType(
            memoryRequirements.memoryTypeBits(),
            VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
            VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
        );
    }

    private VkMemoryRequirements createMemoryRequirements() {
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
        VK10.vkGetBufferMemoryRequirements( device.getLogicalDevice(), bufferAddress.getValue(), memoryRequirements );
        return memoryRequirements;
    }

    private VulkanAddress createBuffer() {
        return VulkanHelper.createAddress(
            () -> VkBufferCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO )
                .pNext( VK10.VK_NULL_HANDLE )
                .size( vertexBuffer.remaining() )
                .usage( VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT )
                .sharingMode( device.isSingleCommandPoolUsed()
                    ? VK10.VK_SHARING_MODE_EXCLUSIVE
                    : VK10.VK_SHARING_MODE_CONCURRENT )
                .flags( 0 ),
            ( createInfo, address ) ->
                    VK10.vkCreateBuffer( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanVertexBuffer( device, resultCode )
        );
    }

    public void close() {
        VK10.vkDestroyBuffer( device.getLogicalDevice(), bufferAddress.getValue(), null );
        VK10.vkFreeMemory(device.getLogicalDevice(), allocatedMemoryAddress.getValue(), null );
    }
}
