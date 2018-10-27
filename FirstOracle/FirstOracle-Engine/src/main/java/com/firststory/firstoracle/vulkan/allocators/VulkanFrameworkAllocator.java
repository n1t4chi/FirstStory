/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.VulkanDebugCallback;
import com.firststory.firstoracle.vulkan.VulkanWindowSurface;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanRenderingContext;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;

/**
 * @author n1t4chi
 */
public class VulkanFrameworkAllocator {
    
    private final VulkanImmutableObjectsRegistry< VulkanWindowSurface > windowSurfaces = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanPhysicalDevice > physicalDevices = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanDeviceAllocator > deviceAllocators = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanRenderingContext > renderingContexts = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanDebugCallback > debugCallbacks = new VulkanImmutableObjectsRegistry<>();
    
    public void dispose() {
        renderingContexts.forEach( this::deregisterRenderingContext );
        physicalDevices.forEach( this::deregisterPhysicalDevice );
        deviceAllocators.forEach( this::deregisterPhysicalDeviceAllocator );
        windowSurfaces.forEach( this::deregisterWindowSuface );
        debugCallbacks.forEach( this::deregisterDebugCallback );
    }
    
    public VulkanDebugCallback createDebugCallback( VkInstance instance ) {
        return debugCallbacks.register( () -> new VulkanDebugCallback( this, instance ) );
    }
    
    public void deregisterDebugCallback( VulkanDebugCallback callback ) {
        debugCallbacks.deregister( callback, VulkanDebugCallback::disposeUnsafe );
    }
    
    public VulkanRenderingContext createRenderingContext( VulkanPhysicalDevice device ) {
        return renderingContexts.register( () -> new VulkanRenderingContext( this, device ) );
    }
    
    public void deregisterRenderingContext( VulkanRenderingContext context ) {
        renderingContexts.deregister( context, VulkanRenderingContext::disposeUnsafe );
    }
    
    public VulkanDeviceAllocator createPhysicalDeviceAllocator( VulkanPhysicalDevice device ) {
        return deviceAllocators.register( () -> new VulkanDeviceAllocator( this, device ) );
    }
    
    public void deregisterPhysicalDeviceAllocator( VulkanDeviceAllocator deviceAllocator ) {
        deviceAllocators.deregister( deviceAllocator, VulkanDeviceAllocator::disposeUnsafe );
    }
    
    public VulkanPhysicalDevice createPhysicalDevice(
        long deviceBuffer,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface windowSurface
    ) {
        return physicalDevices.register( () -> new VulkanPhysicalDevice(
            this,
            deviceBuffer,
            instance,
            validationLayerNamesBuffer,
            windowSurface
        ) );
    }
    
    public void deregisterPhysicalDevice( VulkanPhysicalDevice device ) {
        physicalDevices.deregister( device, VulkanPhysicalDevice::disposeUnsafe );
    }
    
    public VulkanWindowSurface createWindowSurface( VkInstance instance, WindowContext window ) {
        return windowSurfaces.register( () -> new VulkanWindowSurface( this, instance, window ) );
    }
    
    public void deregisterWindowSuface( VulkanWindowSurface surface ) {
        windowSurfaces.deregister( surface, VulkanWindowSurface::disposeUnsafe );
    }
    
}
