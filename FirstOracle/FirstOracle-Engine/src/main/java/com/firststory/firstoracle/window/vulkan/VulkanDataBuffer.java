/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanMemoryExcpetion;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanVertexBuffer;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotMapVulkanMemoryException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBuffer< Data > implements DataBuffer< Data > {
    
    private final VulkanPhysicalDevice device;
    private final VulkanDataBufferLoader loader;
    private final int[] usageFlags;
    private final int[] requiredMemoryTypeFlags;
    private VulkanAddress bufferAddress = VulkanAddress.createNull();
    private VkMemoryRequirements memoryRequirements;
    private VulkanMemoryType usedMemoryType;
    private VkMemoryAllocateInfo allocateInfo;
    private VulkanAddress allocatedMemoryAddress = VulkanAddress.createNull();
    private VulkanDataBuffer stagingBuffer;
    private int dataCount;
    private int dataSize;
    
    VulkanDataBuffer(
        VulkanPhysicalDevice device,
        VulkanDataBufferLoader loader,
        int[] usageFlags,
        int[] requiredMemoryTypeFlags
    )
    {
        this.device = device;
        this.loader = loader;
        this.usageFlags = usageFlags;
        this.requiredMemoryTypeFlags = requiredMemoryTypeFlags;
    }
    
    @Override
    public boolean isLoaded() {
        return !( bufferAddress.isNull() || stagingBuffer == null );
    }
    
    @Override
    public boolean isCreated() {
        return true;
    }
    
    @Override
    public void create() throws CannotCreateBufferException {}
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        loader.bind( this );
    }
    
    @Override
    public void load( Data bufferData ) throws BufferNotCreatedException {
    
    }
    
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
    
    public int getLength() {
        return dataSize * dataCount;
    }
    
    public void load( float[] dataArray ) {
//        this.data = dataArray;
//        size = caluclateLength();
        throw new UnsupportedOperationException( "asfasdfasf" );
    }
    
    VulkanDataBuffer getStagingBuffer() {
        return stagingBuffer;
    }
    
    void setStagingBuffer( VulkanDataBuffer stagingBuffer ) {
        this.stagingBuffer = stagingBuffer;
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
    
    void copyBuffer( VulkanDataBuffer dstBuffer, VulkanCommandPool commandPool ) {
        VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandPool( commandPool.getAddress().getValue() )
            .commandBufferCount( 1 );
        
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT );
        
        PointerBuffer commandBufferBuffer = commandPool.createCommandBufferBuffer( allocInfo, 1 );
        VkCommandBuffer commandBuffer = new VkCommandBuffer( commandBufferBuffer.get( 0 ), device.getLogicalDevice() );
        VK10.vkBeginCommandBuffer( commandBuffer, beginInfo );
        
        VkBufferCopy copyRegion = VkBufferCopy.create().srcOffset( 0 ) // Optional
            .dstOffset( 0 ) // Optional
            .size( dstBuffer.getLength() );
        
        VK10.vkCmdCopyBuffer( commandBuffer,
            this.bufferAddress.getValue(),
            dstBuffer.bufferAddress.getValue(),
            VkBufferCopy.create( 1 ).put( 0, copyRegion )
        );
        
        VK10.vkEndCommandBuffer( commandBuffer );
        
        VkSubmitInfo submitInfo = VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( commandBufferBuffer );
        
        VK10.vkQueueSubmit( commandPool.getUsedQueueFamily().getQueue(), submitInfo, VK10.VK_NULL_HANDLE );
        commandPool.getUsedQueueFamily().waitForQueue();
        VK10.vkFreeCommandBuffers(
            device.getLogicalDevice(),
            commandPool.getAddress().getValue(),
            commandBufferBuffer
        );
    }
    
    VulkanAddress getBufferAddress() {
        return bufferAddress;
    }
    
    void mapMemory( float[] dataArray ) {
        copyMemory( dataArray );
        unmapMemory();
    }
    
    void mapMemory( byte[] data ) {
        copyMemory( data );
        unmapMemory();
    }
    
    private void copyMemory( byte[] dataArray ) {
        ByteBuffer vertexBuffer = createVertexDataBuffer( dataArray );
        MemoryUtil.memCopy( MemoryUtil.memAddress( vertexBuffer ), mapMemory().getValue(), vertexBuffer.remaining() );
        MemoryUtil.memFree( vertexBuffer );
    }
    
    private void copyMemory( float[] dataArray ) {
        ByteBuffer vertexBuffer = createVertexDataBuffer( dataArray );
        MemoryUtil.memCopy( MemoryUtil.memAddress( vertexBuffer ), mapMemory().getValue(), vertexBuffer.remaining() );
        MemoryUtil.memFree( vertexBuffer );
    }
    
    private VulkanAddress mapMemory() {
        return VulkanHelper.createAddressViaBuffer( address -> VK10.vkMapMemory( device.getLogicalDevice(),
            allocatedMemoryAddress.getValue(),
            0,
            allocateInfo.allocationSize(),
            0,
            address
        ), resultCode -> new CannotMapVulkanMemoryException( device, resultCode ) );
    }
    
    private void unmapMemory() {
        VK10.vkUnmapMemory( device.getLogicalDevice(), allocatedMemoryAddress.getValue() );
    }
    
    private ByteBuffer createVertexDataBuffer( float[] data ) {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( getLength() );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
    }
    
    private ByteBuffer createVertexDataBuffer( byte[] data ) {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( getLength() );
        vertexDataBuffer.put( data );
        return vertexDataBuffer;
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
                    .size( getLength() )
                    .usage( VulkanHelper.flagsToInt( usageFlags ) )
                    .flags( 0 );
                if ( device.isSingleCommandPoolUsed() ) {
                    createInfo.sharingMode( VK10.VK_SHARING_MODE_EXCLUSIVE );
                } else {
                    createInfo.sharingMode( VK10.VK_SHARING_MODE_CONCURRENT )
                        .pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() );
                }
                return createInfo;
            },
            ( createInfo, address ) -> VK10.vkCreateBuffer( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanVertexBuffer( device, resultCode )
        );
    }
}
