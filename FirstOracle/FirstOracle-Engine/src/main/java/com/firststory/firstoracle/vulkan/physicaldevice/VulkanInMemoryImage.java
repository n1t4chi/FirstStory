/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanImageException;
import com.firststory.firstoracle.vulkan.exceptions.CannotTransitionVulkanImage;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandBuffer;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanInMemoryImage extends VulkanImage {
    private final VulkanImageLinearMemoryManager manager;
    private VulkanImageLinearMemory linearMemory;
    private int width;
    private int height;
    
    public VulkanInMemoryImage(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanImageLinearMemoryManager manager
    ) {
        super( allocator, device );
        this.manager = manager;
    }
    
    public VulkanInMemoryImage update(
        int width,
        int height,
        int format,
        int tiling,
        int[] usageFlags,
        int[] desiredMemoryFlags
    ) {
        width = Math.max( width, 1 );
        height = Math.max( height, 1 );
        updateAddress(
            createImage(
                getDevice(),
                width,
                height,
                format,
                tiling,
                usageFlags
            )
        );
        linearMemory = bindImageMemory( getDevice(), desiredMemoryFlags, getAddress() );
        this.width = width;
        this.height = height;
        return this;
    }

    void createMipMaps() {
        getDevice().getTextureTransferCommandPool().addTransferCommands( commandBuffer -> {
            var barrier = createImageMemoryBarrier( VkImageSubresourceRange.calloc()
                .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                .baseArrayLayer( 0 )
                .layerCount( 1 )
                .levelCount( 1 )
            );
    
            var mipLevels = calculateMipLevels();
            var mipWidth = width;
            var mipHeight = height;
            for ( var index = 1; index < mipLevels; index++ ) {
                barrier.subresourceRange().baseMipLevel( index-1 );
                barrier
                    .oldLayout( VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL )
                    .newLayout( VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL )
                    .srcAccessMask( VK10.VK_ACCESS_TRANSFER_WRITE_BIT )
                    .dstAccessMask( VK10.VK_ACCESS_TRANSFER_READ_BIT )
                ;
                invokePipelineBarrier(
                    VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
                    VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
                    commandBuffer,
                    barrier
                );
    
                invokeBlitImage( commandBuffer, mipWidth, mipHeight, index );
                
                barrier
                    .oldLayout( VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL )
                    .newLayout( VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL )
                    .srcAccessMask( VK10.VK_ACCESS_TRANSFER_READ_BIT )
                    .dstAccessMask( VK10.VK_ACCESS_SHADER_READ_BIT )
                ;
                invokePipelineBarrier(
                    VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
                    VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
                    commandBuffer,
                    barrier
                );
    
                if ( mipWidth > 1 ) {
                    mipWidth /= 2;
                }
                if ( mipHeight > 1 ) {
                    mipHeight /= 2;
                }
            }
    
            barrier.subresourceRange().baseMipLevel( mipLevels-1 );
            barrier
                .oldLayout( VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                .newLayout( VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL )
                .srcAccessMask( VK10.VK_ACCESS_TRANSFER_WRITE_BIT )
                .dstAccessMask( VK10.VK_ACCESS_SHADER_READ_BIT )
            ;
            invokePipelineBarrier(
                VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
                VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
                commandBuffer,
                barrier
            );
        } );
    }

    VulkanImageView createImageView( int format, int aspectMask ) {
        return super.createImageView( format, aspectMask, calculateMipLevels() );
    }
    
    void dispose() {
        getAllocator().deregisterInMemoryImage( this );
    }
    
    public void disposeUnsafe() {
        VK10.vkDestroyImage( getDevice().getLogicalDevice(), getAddress().getValue(), null );
        updateAddress( new VulkanAddress() );
        width = 0;
        height = 0;
    }
    
    void transitionImageLayout(
        int format,
        int oldLayout,
        int newLayout
    ) {
        int srcAccessMask;
        int dstAccessMask;
        int srcStageMask;
        int dstStageMask;
    
        if (oldLayout == VK10.VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
            srcAccessMask = 0;
            dstAccessMask = VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
        
            srcStageMask = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            dstStageMask = VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
        } else if (oldLayout == VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
            srcAccessMask = VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
            dstAccessMask = VK10.VK_ACCESS_SHADER_READ_BIT;
        
            srcStageMask = VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
            dstStageMask = VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
        } else if (oldLayout == VK10.VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            srcAccessMask = 0;
            dstAccessMask = VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;
        
            srcStageMask = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            dstStageMask = VK10.VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
        } else if (oldLayout == VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            srcAccessMask = VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
            dstAccessMask = VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;
    
            srcStageMask = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            dstStageMask = VK10.VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
        } else if (oldLayout == VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL && newLayout == VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
            srcAccessMask = VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;
            dstAccessMask = VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
    
            srcStageMask = VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
            dstStageMask = VK10.VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
        } else {
            throw new CannotTransitionVulkanImage( getDevice(), this, format, oldLayout, newLayout );
        }
        
        
        getDevice().getTextureTransferCommandPool().addTransferCommands( commandBuffer ->
            invokePipelineBarrier(
                srcStageMask,
                dstStageMask,
                commandBuffer,
                createBarrierBuffer(
                    oldLayout,
                    newLayout,
                    format,
                    srcAccessMask,
                    dstAccessMask
                )
            )
        );
        getDevice().getTextureTransferCommandPool().executeTransfersAndForget();
    }
    
    private VulkanAddress createImage(
        VulkanPhysicalDevice device,
        int width,
        int height,
        int format,
        int tiling,
        int[] usageFlags
    ) {
        var imageCreateInfo = VkImageCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO )
            .imageType( VK10.VK_IMAGE_TYPE_2D )
            .extent( VkExtent3D.calloc().set( width, height, 1 ) )
            .mipLevels( calculateMipLevels( width, height ) )
            .arrayLayers( 1 )
            .format( format )
            .tiling( tiling )
            .initialLayout( VK10.VK_IMAGE_LAYOUT_UNDEFINED )
            .usage( VulkanHelper.flagsToInt( usageFlags ) )
            .sharingMode( device.isSingleQueueFamilyUsed() ? VK10.VK_SHARING_MODE_EXCLUSIVE : VK10.VK_SHARING_MODE_CONCURRENT )
            .pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .flags( 0 );
    
        return VulkanHelper.createAddress( address -> VK10.vkCreateImage(
                device.getLogicalDevice(),
                imageCreateInfo,
                null,
                address
            ),
            resultCode -> new CannotCreateVulkanImageException( device, resultCode )
        );
    }
    
    private int calculateMipLevels( int width, int height ) {
        return floorLog( Math.max( width, height )) + 1;
    }
    
    private int floorLog( int number ) {
        return 31 - Integer.numberOfLeadingZeros( number );
    }
    
    private VulkanImageLinearMemory bindImageMemory(
        VulkanAddress image,
        VkMemoryRequirements memoryRequirements,
        VulkanMemoryType memoryType
    ) {
        return manager.bindImageMemory( image, memoryRequirements, memoryType );
    }
    
    private VulkanImageLinearMemory bindImageMemory( VulkanPhysicalDevice device, int[] desiredMemoryFlags, VulkanAddress image ) {
        var memoryRequirements = VkMemoryRequirements.calloc();
        VK10.vkGetImageMemoryRequirements( device.getLogicalDevice(), image.getValue(), memoryRequirements );
    
        var memoryType = device.selectMemoryType( memoryRequirements.memoryTypeBits(),
            desiredMemoryFlags
        );
        return bindImageMemory( image, memoryRequirements, memoryType );
    }
    
    private void invokeBlitImage( VulkanTransferCommandBuffer commandBuffer, int mipWidth, int mipHeight, int index ) {
        var minw2 = Math.max( 1, mipWidth / 2 );
        var miph2 = Math.max( 1, mipHeight / 2 );
        var blit = VkImageBlit.calloc()
            .srcOffsets( 0, VkOffset3D.calloc().set( 0,0,0 ) )
            .srcOffsets( 1, VkOffset3D.calloc().set( mipWidth, mipHeight, 1 ) )
            .srcSubresource( VkImageSubresourceLayers.calloc()
                .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                .mipLevel( index - 1 )
                .baseArrayLayer( 0 )
                .layerCount( 1 )
            )
            .dstOffsets( 0,  VkOffset3D.calloc().set( 0, 0, 0 ) )
            .dstOffsets( 1, VkOffset3D.calloc().set( minw2, miph2, 1 ) )
            .dstSubresource( VkImageSubresourceLayers.calloc()
                .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                .mipLevel( index )
                .baseArrayLayer( 0 )
                .layerCount( 1 )
            )
        ;
        VK10.vkCmdBlitImage( commandBuffer.getCommandBuffer(),
            getAddress().getValue(),
            VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
            getAddress().getValue(),
            VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            VkImageBlit.calloc( 1 ).put( 0, blit ),
            VK10.VK_FILTER_LINEAR
        );
    }
    
    private void invokePipelineBarrier(
        int srcStageMask,
        int dstStageMask,
        VulkanTransferCommandBuffer commandBuffer,
        VkImageMemoryBarrier barrierBuffer
    ) {
        VK10.vkCmdPipelineBarrier(
            commandBuffer.getCommandBuffer(),
            srcStageMask,
            dstStageMask,
            0,
            null,
            null,
            createSingleMemoryBarrierBuffer( barrierBuffer )
        );
    }
    
    private VkImageMemoryBarrier.Buffer createSingleMemoryBarrierBuffer( VkImageMemoryBarrier barrier ) {
        return VkImageMemoryBarrier.calloc( 1 ).put( 0, barrier );
    }
    
    private int calculateMipLevels() {
        return calculateMipLevels( width, height );
    }
    
    private boolean hasStencilComponent( int format ) {
        return format == VK10.VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK10.VK_FORMAT_D24_UNORM_S8_UINT;
    }
    
    private VkImageMemoryBarrier createBarrierBuffer(
        int oldLayout, int newLayout, int format, int srcAccessMask, int dstAccessMask
    ) {
        var aspectColorBit = VK10.VK_IMAGE_ASPECT_COLOR_BIT;
        if (newLayout == VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            aspectColorBit = VK10.VK_IMAGE_ASPECT_DEPTH_BIT;
    
            if (hasStencilComponent( format )) {
                aspectColorBit |= VK10.VK_IMAGE_ASPECT_STENCIL_BIT;
            }
        }
    
        return createImageMemoryBarrier(
                VkImageSubresourceRange.calloc()
                    .aspectMask( aspectColorBit )
                    .baseMipLevel( 0 )
                    .levelCount( calculateMipLevels() )
                    .baseArrayLayer( 0 )
                    .layerCount( 1 )
            )
            .newLayout( newLayout )
            .oldLayout( oldLayout )
            .srcAccessMask( srcAccessMask )
            .dstAccessMask( dstAccessMask )
        ;
    }
    
    private VkImageMemoryBarrier createImageMemoryBarrier( VkImageSubresourceRange subresourceRange ) {
        return VkImageMemoryBarrier.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER )
            .image( getAddress().getValue() )
            .srcQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .dstQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .subresourceRange( subresourceRange );
    }
}
