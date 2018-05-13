/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanImageException;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanTextureLoader implements TextureBufferLoader<VulkanTextureData > {
    
    private final VulkanPhysicalDevice device;
    private final VulkanDataBufferProvider bufferLoader;
    
    VulkanTextureLoader( VulkanPhysicalDevice physicalDevice, VulkanDataBufferProvider bufferLoader ) {
        
        device = physicalDevice;
        this.bufferLoader = bufferLoader;
    }
    
    @Override
    public VulkanTextureData create() {
        return new VulkanTextureData();
    }
    
    @Override
    public void bind( VulkanTextureData textureData ) {
    
    }
    
    @Override
    public void load( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        createTextureImage( textureData, imageBuffer, name );
        createTextureImageView( textureData );
    }
    
    private void createTextureImageView( VulkanTextureData textureData ) {
        textureData.setImageView(
            device.createImageView( textureData.getTextureImage(), VK10.VK_FORMAT_R8G8B8A8_UNORM ) );
    }
    
    private void createTextureImage( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        IntBuffer w = BufferUtils.createIntBuffer( 1 );
        IntBuffer h = BufferUtils.createIntBuffer( 1 );
        IntBuffer c = BufferUtils.createIntBuffer( 1 );
        ByteBuffer pixels = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, STBImage.STBI_rgb_alpha );
        if ( pixels == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        int width = w.get( 0 );
        int height = h.get( 0 );
        textureData.setWidth( width );
        textureData.setHeight( height );
        textureData.setBuffer( bufferLoader.createMappableBuffer() );
        textureData.getBuffer().createBuffer( pixels.capacity(), 1 );
        textureData.getBuffer().load( pixels );
        textureData.getBuffer().bind();
        
        createImage( textureData );
    
        initialTransitionImageLayout(
            textureData,
            VK10.VK_FORMAT_R8G8B8A8_UNORM
        );
        copyBufferToImage( textureData );
        postCopyTransitionImageLayout(
            textureData,
            VK10.VK_FORMAT_R8G8B8A8_UNORM
        );
        
        textureData.getBuffer().close();
    }
    
    private void copyBufferToImage( VulkanTextureData textureData ) {
        device.getTransferCommandPool().executeQueue( commandBuffer -> {
            VkBufferImageCopy region = VkBufferImageCopy.create()
                .bufferOffset(0)
                .bufferRowLength(0)
                .bufferImageHeight(0)
                .imageSubresource( VkImageSubresourceLayers.create()
                    .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                    .mipLevel(0)
                    .baseArrayLayer(0)
                    .layerCount(1)
                )
                .imageOffset( VkOffset3D.create().set( 0,0,0 ) )
                .imageExtent( VkExtent3D.create().set( textureData.getWidth(), textureData.getHeight(), 1 ) )
            ;
    
            VkBufferImageCopy.Buffer regionBuffer = VkBufferImageCopy.calloc( 1 ).put( 0, region );
    
            VK10.vkCmdCopyBufferToImage(
                commandBuffer.getCommandBuffer(),
                textureData.getBuffer().getBufferAddress().getValue(),
                textureData.getTextureImage().getValue(),
                VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                regionBuffer
            );
            regionBuffer.free();
        } );
    }
    
    private void initialTransitionImageLayout( VulkanTextureData textureData, int format ) {
        transitionImageLayout( textureData,
            format,
            VK10.VK_IMAGE_LAYOUT_UNDEFINED,
            VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            0,
            VK10.VK_ACCESS_TRANSFER_WRITE_BIT,
            VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
            VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
            device.getGraphicCommandPool()
        );
    }
    
    private void postCopyTransitionImageLayout( VulkanTextureData textureData, int format ) {
        transitionImageLayout( textureData,
            format,
            VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
            VK10.VK_ACCESS_TRANSFER_WRITE_BIT,
            VK10.VK_ACCESS_SHADER_READ_BIT,
            VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
            VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
            device.getGraphicCommandPool()
        );
    }
    
    private void transitionImageLayout(
        VulkanTextureData textureData,
        int format,
        int oldLayout,
        int newLayout,
        int srcAccessMask,
        int dstAccessMask,
        int srcStageMask,
        int dstStageMask,
        VulkanCommandPool<?> transferCommandPool
    ) {
    
        transferCommandPool.executeQueue( commandBuffer -> {
            VK10.vkCmdPipelineBarrier( commandBuffer.getCommandBuffer(), srcStageMask,//todo
                dstStageMask,//todo
                0,
                null,
                null,
                createBarrierBuffer( oldLayout, newLayout, textureData.getTextureImage(), srcAccessMask, dstAccessMask )
            );
        } );
    }
    
    private void createImage( VulkanTextureData textureData ) {
        VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO )
            .imageType( VK10.VK_IMAGE_TYPE_2D )
            .extent( VkExtent3D.create().set( textureData.getWidth(), textureData.getHeight(), 1 ) )
            .mipLevels( 1 )
            .arrayLayers( 1 )
            .format( VK10.VK_FORMAT_R8G8B8A8_UNORM )
            .tiling( VK10.VK_IMAGE_TILING_OPTIMAL )
            .initialLayout( VK10.VK_IMAGE_LAYOUT_UNDEFINED )
            .usage( VulkanHelper.flagsToInt( VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT, VK10.VK_IMAGE_USAGE_SAMPLED_BIT ) )
            .sharingMode( device.isSingleCommandPoolUsed()
                ? VK10.VK_SHARING_MODE_EXCLUSIVE
                : VK10.VK_SHARING_MODE_CONCURRENT
            )
            .pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .flags( 0 )
        ;
        
        textureData.setTextureImage( VulkanHelper.createAddress(
            address -> VK10.vkCreateImage( device.getLogicalDevice(), imageCreateInfo, null, address ),
            resultCode -> new CannotCreateVulkanImageException( device, resultCode )
        ) );
        
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
        VK10.vkGetImageMemoryRequirements(
            device.getLogicalDevice(), textureData.getTextureImage().getValue(), memoryRequirements );
        
        VulkanMemoryType memoryType = device.selectMemoryType(
            memoryRequirements.memoryTypeBits(),
            VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
        );
    
        bindImageMemory( textureData, memoryRequirements, memoryType );
    }
    
    private VkImageMemoryBarrier.Buffer createBarrierBuffer(
        int oldLayout, int newLayout, VulkanAddress image, int srcAccessMask, int dstAccessMask
    ) {
        return VkImageMemoryBarrier.create( 1 ).put( 0, VkImageMemoryBarrier.create()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER )
            .oldLayout( oldLayout )
            .newLayout( newLayout )
            .srcQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .dstQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .image( image.getValue() )
            .subresourceRange( VkImageSubresourceRange.create()
                .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                .baseMipLevel( 0 )
                .levelCount( 1 )
                .baseArrayLayer( 0 )
                .layerCount( 1 )
            )
            .srcAccessMask( srcAccessMask )
            .dstAccessMask( dstAccessMask )
        );
    }
    
    private void bindImageMemory(
        VulkanTextureData textureData, VkMemoryRequirements memoryRequirements, VulkanMemoryType memoryType
    ) {
        VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
            .allocationSize( memoryRequirements.size() )
            .memoryTypeIndex( memoryType.getIndex() )
            ;
        
        VulkanAddress textureImageMemory = VulkanHelper.createAddress(
            address -> VK10.vkAllocateMemory( device.getLogicalDevice(), allocateInfo, null, address ),
            resultCode -> new CannotAllocateVulkanImageMemoryException( device, resultCode )
        );
        
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkBindImageMemory(
                device.getLogicalDevice(),
                textureData.getTextureImage().getValue(),
                textureImageMemory.getValue(),
                0
            ), resultCode -> new CannotBindVulkanImageMemoryException( device, resultCode )
        );
    }
    
    @Override
    public void delete( VulkanTextureData textureData ) {
    
    }
    
    @Override
    public void close() {
    
    }
}
