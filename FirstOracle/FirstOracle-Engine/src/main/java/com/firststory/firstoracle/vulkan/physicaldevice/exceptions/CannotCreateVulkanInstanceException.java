/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanInstanceException extends VulkanException {
    
    public CannotCreateVulkanInstanceException( int errorCode ) {
        super( errorCode );
    }
}
