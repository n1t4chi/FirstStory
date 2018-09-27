/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotAcquireNextImageIndexException extends VulkanNextImageIndexException {
    
    public CannotAcquireNextImageIndexException( VulkanPhysicalDevice device, int index, int errorCode ) {
        super( device, index, "Cannot acquire next image index. " +
            "Error code:" + errorCode+"["+VulkanExceptionHelper.parseResultCode( errorCode )+"]" );
    }
}
