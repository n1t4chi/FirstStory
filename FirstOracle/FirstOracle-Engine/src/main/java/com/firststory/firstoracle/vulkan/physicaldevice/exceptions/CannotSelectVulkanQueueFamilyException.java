/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotSelectVulkanQueueFamilyException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotSelectVulkanQueueFamilyException( VulkanPhysicalDevice physicalDevice, String familyType ) {
        super( physicalDevice, "Cannot select "+familyType+" queue since there are no suitable candidates." );
    }
}
