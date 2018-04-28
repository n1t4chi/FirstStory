/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.WindowContext;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanWindowSurfaceException;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class VulkanWindowSurface {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanWindowSurface.class );
    
    public static VulkanWindowSurface create( VkInstance instance, WindowContext window ) {
        long[] pSurface = new long[1];
        if ( GLFWVulkan.glfwCreateWindowSurface( instance, window.getAddress(), null, pSurface ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanWindowSurfaceException( instance, window );
        }
        return new VulkanWindowSurface( pSurface[0] );
    }
    
    private final long address;
    
    private VulkanWindowSurface( long address ) {
        this.address = address;
    }
    
    void dispose( VkInstance instance ) {
        KHRSurface.vkDestroySurfaceKHR( instance, address, null );
    }
    
    long getAddress() {
        return address;
    }
    
}
