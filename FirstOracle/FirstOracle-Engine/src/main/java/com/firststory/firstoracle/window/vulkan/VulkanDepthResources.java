/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VK10;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
class VulkanDepthResources {
    
    private final VulkanFormatProperty depthFormat;
    private VulkanInMemoryImage depthImage;
    private VulkanImageView depthImageView;
    private final VulkanPhysicalDevice device;
    
    VulkanDepthResources( VulkanPhysicalDevice device ) {
        this.device = device;
        depthFormat = findDepthFormat();
    }
    
    void update( VulkanSwapChain swapChain ) {
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
    
    VulkanFormatProperty getDepthFormat() {
        return depthFormat;
    }
    
    private VulkanImageView createDepthImageView( VulkanFormatProperty depthFormat, VulkanImage depthImage ) {
        return depthImage.createImageView(
            depthFormat.getFormat(), VK10.VK_IMAGE_ASPECT_DEPTH_BIT );
    }
    
    private VulkanInMemoryImage createDepthImage( VulkanSwapChain swapChain, VulkanFormatProperty depthFormat ) {
        return new VulkanInMemoryImage(
            device,
            swapChain.getExtent().width(),
            swapChain.getExtent().height(),
            depthFormat.getFormat(),
            VK10.VK_IMAGE_TILING_OPTIMAL,
            new int[] { VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT },
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
    
    void close() {
        depthImage.close();
    }
}
