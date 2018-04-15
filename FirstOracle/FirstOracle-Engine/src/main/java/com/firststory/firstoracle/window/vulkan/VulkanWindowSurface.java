/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;

/**
 * @author n1t4chi
 */
public class VulkanWindowSurface {
    
    public static VulkanWindowSurface create( VkInstance instance, WindowContext window ){
        long[] pSurface = new long[1];
        if( GLFWVulkan.glfwCreateWindowSurface( instance, window.getAddress(),null, pSurface ) != VK10.VK_SUCCESS ){
            throw new CannotCreateVulkanWindowSurfaceException( instance, window );
        }
        return new VulkanWindowSurface( pSurface[0] );
    }
    private final long address;
    
    private VulkanWindowSurface( long address ) {
        this.address = address;
    }
    
    public long getAddress() {
        return address;
    }
    
    public void close( VkInstance instance) {
        KHRSurface.vkDestroySurfaceKHR( instance, address, null );
    }
}
