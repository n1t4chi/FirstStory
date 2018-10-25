/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class CannotSelectSuitableMemoryTypeException extends VulkanException {
    
    public CannotSelectSuitableMemoryTypeException( VulkanPhysicalDevice device, int type, int... flags ) {
        super( "Cannot find suitable memory type for device: " + device + " with memory types:" +
            device.memoryTypesToString() + "." + " Expected type:" + type + " and flags:" + Arrays.toString( flags ) );
    }
}
