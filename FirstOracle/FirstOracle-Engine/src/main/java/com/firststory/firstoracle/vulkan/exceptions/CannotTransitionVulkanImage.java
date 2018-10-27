/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanInMemoryImage;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotTransitionVulkanImage extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotTransitionVulkanImage(
        VulkanPhysicalDevice device,
        VulkanInMemoryImage image,
        int format,
        int oldLayout,
        int newLayout
    ) {
        super( device,
            "Cannot transition vulkan image: " + image +
            " from: " + oldLayout + " to: " + newLayout +
            " for format:" + format
        );
    }
}
