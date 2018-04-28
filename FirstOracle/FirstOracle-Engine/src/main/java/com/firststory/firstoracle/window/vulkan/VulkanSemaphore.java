/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanSemaphoreException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

/**
 * @author n1t4chi
 */
class VulkanSemaphore {
    
    private final VulkanPhysicalDevice device;
    private final long address;
    
    VulkanSemaphore( VulkanPhysicalDevice device ) {
        this.device = device;
        
        this.address = createSemaphore( device );
    }
    
    void dispose() {
        VK10.vkDestroySemaphore( device.getLogicalDevice(), address, null );
    }
    
    public long getAddress() {
        return address;
    }
    
    private VkSemaphoreCreateInfo createSemaphoreCreateInfo() {
        return VkSemaphoreCreateInfo.create().sType( VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO );
    }
    
    private long createSemaphore( VulkanPhysicalDevice device ) {
        long[] address = new long[1];
        if ( VK10.vkCreateSemaphore( device.getLogicalDevice(), createSemaphoreCreateInfo(), null, address ) !=
            VK10.VK_SUCCESS )
        {
            throw new CannotCreateVulkanSemaphoreException( device );
        }
        return address[0];
    }
}
