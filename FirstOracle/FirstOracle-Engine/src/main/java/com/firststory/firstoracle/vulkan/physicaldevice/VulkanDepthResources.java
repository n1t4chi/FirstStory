/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import org.lwjgl.vulkan.VK10;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
public class VulkanDepthResources {
    
    private final VulkanFormatProperty depthFormat;
    private final VulkanPhysicalDevice device;
    private final VulkanDeviceAllocator allocator;
    private VulkanInMemoryImage depthImage;
    private VulkanImageView depthImageView;
    
    public VulkanDepthResources(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
        depthFormat = findDepthFormat();
    }
    
    void update( VulkanSwapChain swapChain ) {
        disposeUnsafe();
        depthImage = createDepthImage( swapChain, depthFormat );
        depthImageView = createDepthImageView( depthFormat, depthImage );
        depthImage.transitionImageLayout(
            depthFormat.getFormat(),
            VK10.VK_IMAGE_LAYOUT_UNDEFINED,
            VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL
        );
    }
    
    VulkanImageView getDepthImageView() {
        return depthImageView;
    }
    
    public VulkanFormatProperty getDepthFormat() {
        return depthFormat;
    }
    
    private VulkanImageView createDepthImageView( VulkanFormatProperty depthFormat, VulkanInMemoryImage depthImage ) {
        return depthImage.createImageView( depthFormat.getFormat(), VK10.VK_IMAGE_ASPECT_DEPTH_BIT, 1 );
    }
    
    private VulkanInMemoryImage createDepthImage( VulkanSwapChain swapChain, VulkanFormatProperty depthFormat ) {
        return allocator.createInMemoryImage(
            swapChain.getExtent().width(),
            swapChain.getExtent().height(),
            depthFormat.getFormat(),
            VK10.VK_IMAGE_TILING_OPTIMAL,
            new int[] { VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT },
            new int[] { VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT }
        );
    }
    
    private VulkanFormatProperty findDepthFormat() {
        return device.findFormatProperty( new HashSet<>( Arrays.asList(
            VK10.VK_FORMAT_D32_SFLOAT,
            VK10.VK_FORMAT_D32_SFLOAT_S8_UINT,
            VK10.VK_FORMAT_D24_UNORM_S8_UINT
        ) ), VK10.VK_IMAGE_TILING_OPTIMAL, VK10.VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT );
    }
    
    void dispose() {
        allocator.deregisterDepthResource( this );
    }
    
    public void disposeUnsafe() {
        if( depthImage != null ) {
            depthImage.dispose();
        }
        if( depthImageView != null ) {
            depthImageView.dispose();
        }
    }
}
