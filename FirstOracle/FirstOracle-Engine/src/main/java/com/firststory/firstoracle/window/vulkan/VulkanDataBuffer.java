/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.*;
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
public class VulkanDataBuffer extends ArrayBuffer< VulkanDataBuffer > {
    
    static final int ATTRIBUTES_POSITION = 2;
    static final int ATTRIBUTES_COLOUR = 3;
    private static final int ATTRIBUTES = ATTRIBUTES_POSITION + ATTRIBUTES_COLOUR;
    static final int VERTEX_DATA_SIZE = ATTRIBUTES * 4;
    static final int VERTEX_POSITION_DATA_SIZE = ATTRIBUTES_POSITION * 4;
    static final int VERTEX_COLOUR_DATA_SIZE = ATTRIBUTES_COLOUR * 4;
    
    private final VulkanPhysicalDevice device;
    private final ArrayBufferProvider< VulkanDataBuffer > loader;
    private final int[] usageFlags;
    private final int[] requiredMemoryTypeFlags;
    private int size;
    private VulkanAddress bufferAddress = VulkanAddress.createNull();
    private VkMemoryRequirements memoryRequirements;
    private VulkanMemoryType usedMemoryType;
//    private ByteBuffer vertexBuffer;
    //private float[] data;
    private VkMemoryAllocateInfo allocateInfo;
    private VulkanAddress allocatedMemoryAddress = VulkanAddress.createNull();
    private VulkanDataBuffer stagingBuffer;
    
    VulkanDataBuffer(
        VulkanPhysicalDevice device,
        ArrayBufferProvider< VulkanDataBuffer > loader,
        int[] usageFlags,
        int[] requiredMemoryTypeFlags
    ) {
        super( loader );
        this.device = device;
        this.loader = loader;
        this.usageFlags = usageFlags;
        this.requiredMemoryTypeFlags = requiredMemoryTypeFlags;
    }
    
//    @Override
//    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
//        if( bufferAddress.isNull() ) {
//            throw new BufferNotLoadedException();
//        }
//        if( !allocatedMemoryAddress.isNull() ) {
//            return;
//        }
//    }
    
    @Override
    public void assertCreated() {
    
    }
    
    @Override
    public void assertLoaded() {
        if( bufferAddress.isNull() || stagingBuffer == null ) {
            throw new BufferNotLoadedException();
        }
    }
    
    @Override
    public VulkanDataBuffer getContext() {
        return this;
    }
    
    @Override
    public int getLength() {
        return size;
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
    public void create() throws CannotCreateBufferException {
    
    }
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        loader.bind( this );
    }
    
    @Override
    public void delete() throws BufferNotCreatedException {
        close();
    }
    
    @Override
    public void load( float[] dataArray ) {
//        this.data = dataArray;
//        size = caluclateLength();
        throw new UnsupportedOperationException( "asfasdfasf" );
    }
    
    void setStagingBuffer( VulkanDataBuffer stagingBuffer ) {
        this.stagingBuffer = stagingBuffer;
    }
    VulkanDataBuffer getStagingBuffer() {
        return stagingBuffer;
    }
    
    void load( int arrayLength, int dataSize ) {
        this.size = arrayLength / dataSize;
//        vertexBuffer = createVertexDataBuffer();
        
        bufferAddress = createBuffer();
        memoryRequirements = createMemoryRequirements();
        usedMemoryType = selectRequiredMemoryType();
    
        allocateInfo = createMemoryAllocateInfo();
        allocatedMemoryAddress = allocateMemory();
        bindMemoryToBuffer();
    }
    
    public void close() {
        if( !bufferAddress.isNull() ) {
            VK10.vkDestroyBuffer( device.getLogicalDevice(), bufferAddress.getValue(), null );
            bufferAddress.setNull();
        }
        if( !allocatedMemoryAddress.isNull() ) {
        VK10.vkFreeMemory(device.getLogicalDevice(), allocatedMemoryAddress.getValue(), null );
            allocatedMemoryAddress.setNull();
        }
    }
    
    void copyBuffer( VulkanDataBuffer dstBuffer, VulkanCommandPool commandPool ) {
        int size = dstBuffer.size * VERTEX_DATA_SIZE;
    
        VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandPool( commandPool.getAddress().getValue() )
            .commandBufferCount( 1 );
    
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT );
    
        PointerBuffer commandBufferBuffer = commandPool.createCommandBufferBuffer( allocInfo, 1 );
        VkCommandBuffer commandBuffer = new VkCommandBuffer( commandBufferBuffer.get(0),
            device.getLogicalDevice() );
        VK10.vkBeginCommandBuffer( commandBuffer, beginInfo );
    
        VkBufferCopy copyRegion = VkBufferCopy.create().srcOffset( 0 ) // Optional
            .dstOffset( 0 ) // Optional
            .size( size );
    
        VK10.vkCmdCopyBuffer(
            commandBuffer,
            this.bufferAddress.getValue(),
            dstBuffer.bufferAddress.getValue(),
            VkBufferCopy.create(1).put( 0, copyRegion )
        );
    
        VK10.vkEndCommandBuffer( commandBuffer );
    
        VkSubmitInfo submitInfo = VkSubmitInfo.create()
        .sType ( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
        .pCommandBuffers( commandBufferBuffer );
    
        VK10.vkQueueSubmit( commandPool.getUsedQueueFamily().getQueue()  , submitInfo, VK10.VK_NULL_HANDLE);
        commandPool.getUsedQueueFamily().waitForQueue();
    
        VK10.vkFreeCommandBuffers(device.getLogicalDevice(), commandPool.getAddress().getValue(), commandBufferBuffer );
    }
    
    VulkanAddress getBufferAddress() {
        return bufferAddress;
    }
    
    void mapMemory( float[] dataArray ){
        copyMemory( dataArray );
        unmapMemory();
    }
    
    private void copyMemory( float[] dataArray ) {
        ByteBuffer vertexBuffer = createVertexDataBuffer( dataArray );
        MemoryUtil.memCopy(
            MemoryUtil.memAddress( vertexBuffer ), mapMemory().getValue(), vertexBuffer.remaining());
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
    
    private ByteBuffer createVertexDataBuffer( float[] data ) {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( size * VERTEX_DATA_SIZE );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
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
            requiredMemoryTypeFlags
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
                .size( size )
                .usage( VulkanHelper.flagsToInt( usageFlags ) )
                .sharingMode( device.isSingleCommandPoolUsed()
                    ? VK10.VK_SHARING_MODE_EXCLUSIVE
                    : VK10.VK_SHARING_MODE_CONCURRENT )
                .flags( 0 ),
            ( createInfo, address ) ->
                    VK10.vkCreateBuffer( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanVertexBuffer( device, resultCode )
        );
    }
}