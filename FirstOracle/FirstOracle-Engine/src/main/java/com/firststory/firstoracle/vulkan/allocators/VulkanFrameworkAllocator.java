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
    
    private final VulkanImmutableObjectsRegistry< VulkanWindowSurface > windowSurfaces = new VulkanImmutableObjectsRegistry<>( VulkanWindowSurface::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanPhysicalDevice > physicalDevices = new VulkanImmutableObjectsRegistry<>( VulkanPhysicalDevice::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDeviceAllocator > deviceAllocators = new VulkanImmutableObjectsRegistry<>( VulkanDeviceAllocator::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanRenderingContext > renderingContexts = new VulkanImmutableObjectsRegistry<>( VulkanRenderingContext::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDebugCallback > debugCallbacks = new VulkanImmutableObjectsRegistry<>( VulkanDebugCallback::disposeUnsafe );
    
    public void dispose() {
        renderingContexts.dispose();
        physicalDevices.dispose();
        deviceAllocators.dispose();
        windowSurfaces.dispose();
        debugCallbacks.dispose();
    }
    
    public VulkanDebugCallback createDebugCallback( VkInstance instance ) {
        return debugCallbacks.register( () -> new VulkanDebugCallback( this, instance ) );
    }
    
    public void deregisterDebugCallback( VulkanDebugCallback callback ) {
        debugCallbacks.deregister( callback );
    }
    
    public VulkanRenderingContext createRenderingContext( VulkanPhysicalDevice device ) {
        return renderingContexts.register( () -> new VulkanRenderingContext( this, device ) );
    }
    
    public void deregisterRenderingContext( VulkanRenderingContext context ) {
        renderingContexts.deregister( context );
    }
    
    public VulkanDeviceAllocator createPhysicalDeviceAllocator( VulkanPhysicalDevice device ) {
        return deviceAllocators.register( () -> new VulkanDeviceAllocator( this, device ) );
    }
    
    void deregisterPhysicalDeviceAllocator( VulkanDeviceAllocator deviceAllocator ) {
        deviceAllocators.deregister( deviceAllocator );
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
        physicalDevices.deregister( device );
    }
    
    public VulkanWindowSurface createWindowSurface( VkInstance instance, WindowContext window ) {
        return windowSurfaces.register( () -> new VulkanWindowSurface( this, instance, window ) );
    }
    
    public void deregisterWindowSuface( VulkanWindowSurface surface ) {
        windowSurfaces.deregister( surface );
    }
    
}
