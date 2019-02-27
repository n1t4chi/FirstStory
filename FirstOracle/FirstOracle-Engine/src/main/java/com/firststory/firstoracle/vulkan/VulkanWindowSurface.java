/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.allocators.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanWindowSurfaceException;
import com.firststory.firstoracle.window.WindowContext;
import com.firststory.firsttools.FirstToolsConstants;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkInstance;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class VulkanWindowSurface {
    
    private static final Logger logger = FirstToolsConstants.getLogger( VulkanWindowSurface.class );
    
    private final VulkanFrameworkAllocator allocator;
    private final VkInstance instance;
    private final VulkanAddress address;
    
    public VulkanWindowSurface(
        VulkanFrameworkAllocator allocator,
        VkInstance instance,
        WindowContext window
    ) {
        this.allocator = allocator;
        this.instance = instance;
        this.address = createWindowSurface( instance, window);
    }
    
    private VulkanAddress createWindowSurface(
        VkInstance instance,
        WindowContext window
    ) {
        return VulkanHelper.createAddress(
            address -> GLFWVulkan.glfwCreateWindowSurface( instance, window.getAddress(), null, address ),
            resultCode -> new CannotCreateVulkanWindowSurfaceException( resultCode, instance, window )
        );
    }
    
    void dispose() {
        allocator.deregisterWindowSuface( this );
    }
    
    public void disposeUnsafe() {
        KHRSurface.vkDestroySurfaceKHR( instance, address.getValue(), null );
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
}
