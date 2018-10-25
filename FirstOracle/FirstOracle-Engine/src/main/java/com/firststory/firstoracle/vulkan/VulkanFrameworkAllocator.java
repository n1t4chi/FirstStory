/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanRenderingContext;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * @author n1t4chi
 */
public class VulkanFrameworkAllocator {
    
    private final Set< VulkanWindowSurface > windowSurfaces = new HashSet<>();
    private final Set< VulkanPhysicalDevice > physicalDevices = new HashSet<>();
    private final Set< VulkanDeviceAllocator > deviceAllocators = new HashSet<>();
    private final Set< VulkanRenderingContext > renderingContexts = new HashSet<>();
    private final Set< VulkanDebugCallback > debugCallbacks = new HashSet<>();
    
    public void dispose() {
        VulkanHelper.safeForEach( renderingContexts, this::deregisterRenderingContext );
        VulkanHelper.safeForEach( physicalDevices, this::deregisterPhysicalDevice );
        VulkanHelper.safeForEach( deviceAllocators, this::deregisterPhysicalDeviceAllocator );
        VulkanHelper.safeForEach( windowSurfaces, this::deregisterWindowSuface );
        VulkanHelper.safeForEach( debugCallbacks, this::deregisterDebugCallback );
    }
    
    VulkanDebugCallback createDebugCallback( VkInstance instance ) {
        return VulkanHelper.register( debugCallbacks, () -> new VulkanDebugCallback( this, instance ) );
    }
    
    void deregisterDebugCallback( VulkanDebugCallback callback ) {
        VulkanHelper.deregister( debugCallbacks, callback, VulkanDebugCallback::disposeUnsafe );
    }
    
    VulkanRenderingContext createRenderingContext( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( renderingContexts, () -> new VulkanRenderingContext( this, device ) );
    }
    
    public void deregisterRenderingContext( VulkanRenderingContext context ) {
        VulkanHelper.deregister( renderingContexts, context, VulkanRenderingContext::disposeUnsafe );
    }
    
    VulkanDeviceAllocator createPhysicalDeviceAllocator() {
        return VulkanHelper.register( deviceAllocators, () -> new VulkanDeviceAllocator( this ) );
    }
    
    public void deregisterPhysicalDeviceAllocator( VulkanDeviceAllocator deviceAllocator ) {
        VulkanHelper.deregister( deviceAllocators, deviceAllocator, VulkanDeviceAllocator::disposeUnsafe );
    }
    
    VulkanPhysicalDevice createPhysicalDevice(
        long deviceBuffer,
        VulkanDeviceAllocator allocator,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface windowSurface
    ) {
        return VulkanHelper.register( physicalDevices, () -> new VulkanPhysicalDevice(
            this,
            allocator,
            deviceBuffer,
            instance,
            validationLayerNamesBuffer,
            windowSurface
        ) );
    }
    
    public void deregisterPhysicalDevice( VulkanPhysicalDevice device ) {
        VulkanHelper.deregister( physicalDevices, device, VulkanPhysicalDevice::disposeUnsafe );
    }
    
    VulkanWindowSurface createWindowSurface( VkInstance instance, WindowContext window ) {
        return VulkanHelper.register( windowSurfaces, () -> new VulkanWindowSurface( this, instance, window ) );
    }
    
    void deregisterWindowSuface( VulkanWindowSurface surface ) {
        VulkanHelper.deregister( windowSurfaces, surface, VulkanWindowSurface::disposeUnsafe );
    }
    
}
