/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanImageViewException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

/**
 * @author n1t4chi
 */
public class VulkanImageView {
    
    private final VulkanPhysicalDevice device;
    private final VulkanImage image;
    private final VulkanAddress address;
    
    VulkanImageView( VulkanPhysicalDevice device, VulkanImage image, int format, int aspectMask, int mipLevels ) {
        this.device = device;
        this.image = image;
        this.address = createImageView( image, format, aspectMask, mipLevels );
    }
    
    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        result = 31 * result + ( address != null ? address.hashCode() : 0 );
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        VulkanImageView that = ( VulkanImageView ) o;
        
        if ( device != null ? !device.equals( that.device ) : that.device != null ) { return false; }
        return address != null ? address.equals( that.address ) : that.address == null;
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    void close() {
        VK10.vkDestroyImageView( device.getLogicalDevice(), address.getValue(), null );
    }
    
    private VulkanAddress createImageView( VulkanImage image, int format, int aspectMask, int mipLevels ) {
        return VulkanHelper.createAddress(
            () -> VkImageViewCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO )
                .image( image.getAddress().getValue() )
                .viewType( VK10.VK_IMAGE_VIEW_TYPE_2D )
                .format( format )
                .components( VkComponentMapping.create()
                    .a( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .r( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .g( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .b( VK10.VK_COMPONENT_SWIZZLE_IDENTITY ) )
                .subresourceRange( VkImageSubresourceRange.create()
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
