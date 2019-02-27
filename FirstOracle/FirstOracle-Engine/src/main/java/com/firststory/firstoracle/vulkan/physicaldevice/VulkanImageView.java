/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanImageViewException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.util.Objects;

/**
 * @author n1t4chi
 */
public class VulkanImageView {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    
    public VulkanImageView(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanImage image,
        int format,
        int aspectMask,
        int mipLevels
    ) {
        this.allocator = allocator;
        this.device = device;
        this.address = createImageView( image, format, aspectMask, mipLevels );
    }
    
    @Override
    public int hashCode() {
        var result = device != null ? device.hashCode() : 0;
        result = 31 * result + ( address != null ? address.hashCode() : 0 );
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var that = ( VulkanImageView ) o;
        
        if ( !Objects.equals( device, that.device ) ) { return false; }
        return Objects.equals( address, that.address );
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    void dispose() {
        allocator.deregisterImageView( this );
    }
    
    public void disposeUnsafe() {
        VK10.vkDestroyImageView( device.getLogicalDevice(), address.getValue(), null );
    }
    
    private VulkanAddress createImageView( VulkanImage image, int format, int aspectMask, int mipLevels ) {
        return VulkanHelper.createAddress(
            () -> VkImageViewCreateInfo.calloc()
                .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO )
                .image( image.getAddress().getValue() )
                .viewType( VK10.VK_IMAGE_VIEW_TYPE_2D )
                .format( format )
                .components( VkComponentMapping.calloc()
                    .a( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .r( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .g( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .b( VK10.VK_COMPONENT_SWIZZLE_IDENTITY ) )
                .subresourceRange( VkImageSubresourceRange.calloc()
                    .aspectMask( aspectMask )
                    .baseMipLevel( 0 )
                    .levelCount( mipLevels )
                    .baseArrayLayer( 0 )
                    .layerCount( 1 ) ),
            ( createInfo, address ) -> VK10.vkCreateImageView( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanImageViewException( device, resultCode )
        );
    }
}
