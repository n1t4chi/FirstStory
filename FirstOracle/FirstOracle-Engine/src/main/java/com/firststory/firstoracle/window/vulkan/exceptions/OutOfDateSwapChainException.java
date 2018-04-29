/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class OutOfDateSwapChainException extends VulkanNextImageIndexException {
    
    public OutOfDateSwapChainException( VulkanPhysicalDevice physicalDevice, int imageIndex ) {
        super( physicalDevice, imageIndex, "Swap chain is out of date." );
    }
}
