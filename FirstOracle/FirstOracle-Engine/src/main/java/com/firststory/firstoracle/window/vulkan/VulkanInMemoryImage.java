/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanImageException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotTransitionVulkanImage;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanInMemoryImage extends VulkanImage {
    
    private static VulkanAddress createImage(
        VulkanPhysicalDevice device,
        int width,
        int height,
        int format,
        int tiling,
        int[] usageFlags,
        int[] desiredMemoryFlags
    ) {
        VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO )
            .imageType( VK10.VK_IMAGE_TYPE_2D )
            .extent( VkExtent3D.create().set( width, height, 1 ) )
            .mipLevels( 1 )
            .arrayLayers( 1 )
            .format( format )
            .tiling( tiling )
            .initialLayout( VK10.VK_IMAGE_LAYOUT_UNDEFINED )
            .usage( VulkanHelper.flagsToInt( usageFlags ) )
            .sharingMode( device.isSingleCommandPoolUsed() ? VK10.VK_SHARING_MODE_EXCLUSIVE : VK10.VK_SHARING_MODE_CONCURRENT )
            .pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .flags( 0 );
        
        VulkanAddress image = VulkanHelper.createAddress( address -> VK10.vkCreateImage(
            device.getLogicalDevice(),
            imageCreateInfo,
            null,
            address
            ),
            resultCode -> new CannotCreateVulkanImageException( device, resultCode )
        );
        
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
        VK10.vkGetImageMemoryRequirements( device.getLogicalDevice(), image.getValue(), memoryRequirements );
        
        VulkanMemoryType memoryType = device.selectMemoryType( memoryRequirements.memoryTypeBits(),
            desiredMemoryFlags
        );
        
        bindImageMemory( device, image, memoryRequirements, memoryType );
        return image;
    }
    
    private static void bindImageMemory(
        VulkanPhysicalDevice device,
        VulkanAddress image,
        VkMemoryRequirements memoryRequirements,
        VulkanMemoryType memoryType
    ) {
        VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
            .allocationSize( memoryRequirements.size() )
            .memoryTypeIndex( memoryType.getIndex() );
        
        VulkanAddress textureImageMemory = VulkanHelper.createAddress( address -> VK10.vkAllocateMemory(
            device.getLogicalDevice(),
            allocateInfo,
            null,
            address
            ),
            resultCode -> new CannotAllocateVulkanImageMemoryException( device, resultCode )
        );
        
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkBindImageMemory( device.getLogicalDevice(),
                image.getValue(),
                textureImageMemory.getValue(),
                0
            ),
            resultCode -> new CannotBindVulkanImageMemoryException( device, resultCode )
        );
    }
    
    boolean hasStencilComponent( int format ) {
        return format == VK10.VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK10.VK_FORMAT_D24_UNORM_S8_UINT;
    }
    
    VulkanInMemoryImage(
        VulkanPhysicalDevice device,
        int width,
        int height,
        int format,
        int tiling,
        int[] usageFlags,
        int[] desiredMemoryFlags
    ) {
        super( device, createImage( device, width, height, format, tiling, usageFlags, desiredMemoryFlags ) );
    }
    
    void close() {
        VK10.vkDestroyImage( getDevice().getLogicalDevice(), getAddress().getValue(), null );
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
        } else {
            throw new CannotTransitionVulkanImage( getDevice(), this, format, oldLayout, newLayout );
        }
        
        
        getDevice().getTextureTransferCommandPool().executeQueue( commandBuffer -> {
            VK10.vkCmdPipelineBarrier( commandBuffer.getCommandBuffer(),
                srcStageMask,
                dstStageMask,
                0,
                null,
                null,
                createBarrierBuffer( oldLayout, newLayout, format, srcAccessMask, dstAccessMask )
            );
        } );
    }
    
    private VkImageMemoryBarrier.Buffer createBarrierBuffer(
        int oldLayout,
        int newLayout,
        int format,
        int srcAccessMask,
        int dstAccessMask
    ) {
        int aspectColorBit = VK10.VK_IMAGE_ASPECT_COLOR_BIT;
        if (newLayout == VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
            aspectColorBit = VK10.VK_IMAGE_ASPECT_DEPTH_BIT;
    
            if (hasStencilComponent( format )) {
                aspectColorBit |= VK10.VK_IMAGE_ASPECT_STENCIL_BIT;
            }
        }
    
        return VkImageMemoryBarrier.create( 1 ).put( 0, VkImageMemoryBarrier.create()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER )
            .oldLayout( oldLayout )
            .newLayout( newLayout )
            .srcQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .dstQueueFamilyIndex( VK10.VK_QUEUE_FAMILY_IGNORED )
            .image( getAddress().getValue() )
            .subresourceRange( VkImageSubresourceRange.create()
                .aspectMask( aspectColorBit )
                .baseMipLevel( 0 )
                .levelCount( 1 )
                .baseArrayLayer( 0 )
                .layerCount( 1 )
            )
            .srcAccessMask( srcAccessMask )
            .dstAccessMask( dstAccessMask )
        );
    }
}
