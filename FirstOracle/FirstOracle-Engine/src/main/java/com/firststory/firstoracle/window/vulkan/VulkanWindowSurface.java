/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.WindowContext;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanWindowSurfaceException;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkInstance;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class VulkanWindowSurface {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanWindowSurface.class );
    
    public static VulkanWindowSurface create( VkInstance instance, WindowContext window ) {
        return new VulkanWindowSurface( VulkanHelper.createAddress(
            address -> GLFWVulkan.glfwCreateWindowSurface( instance, window.getAddress(), null, address ),
            resultCode -> new CannotCreateVulkanWindowSurfaceException( resultCode, instance, window )
        ) );
    }
    
    private final VulkanAddress address;
    
    private VulkanWindowSurface( VulkanAddress address ) {
        this.address = address;
    }
    
    void dispose( VkInstance instance ) {
        KHRSurface.vkDestroySurfaceKHR( instance, address.getValue(), null );
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
}
