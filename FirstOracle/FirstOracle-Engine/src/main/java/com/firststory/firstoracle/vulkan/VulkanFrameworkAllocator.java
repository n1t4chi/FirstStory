/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.rendering.VulkanRenderingContext;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public class VulkanFrameworkAllocator {
    
    private final List< VulkanWindowSurface > windowSurfaces = new ArrayList<>();
    private final List< VulkanPhysicalDevice > physicalDevices = new ArrayList<>();
    private final List< VulkanInstanceAllocator > deviceAllocators = new ArrayList<>();
    private final List< VulkanRenderingContext > renderingContexts = new ArrayList<>();
    private final List< VulkanDebugCallback > debugCallbacks = new ArrayList<>();
    
    public void dispose() {
        copy( renderingContexts ).forEach( this::deregisterRenderingContext );
        copy( physicalDevices ).forEach( this::deregisterPhysicalDevice );
        copy( deviceAllocators ).forEach( this::deregisterPhysicalDeviceAllocator );
        copy( windowSurfaces ).forEach( this::deregisterWindowSuface );
        copy( debugCallbacks ).forEach( this::deregisterDebugCallback );
    }
    
    VulkanDebugCallback createDebugCallback( VkInstance instance ) {
        return register( debugCallbacks, () -> new VulkanDebugCallback( this, instance ) );
    }
    
    void deregisterDebugCallback( VulkanDebugCallback callback ) {
        deregister( debugCallbacks, callback, VulkanDebugCallback::disposeUnsafe );
    }
    
    VulkanRenderingContext createRenderingContext( VulkanPhysicalDevice device ) {
        return register( renderingContexts, () -> new VulkanRenderingContext( this, device ) );
    }
    
    public void deregisterRenderingContext( VulkanRenderingContext context ) {
        deregister( renderingContexts, context, VulkanRenderingContext::disposeUnsafe );
    }
    
    VulkanInstanceAllocator createPhysicalDeviceAllocator() {
        return register( deviceAllocators, () -> new VulkanInstanceAllocator( this ) );
    }
    
    void deregisterPhysicalDeviceAllocator( VulkanInstanceAllocator deviceAllocator ) {
        deregister( deviceAllocators, deviceAllocator, VulkanInstanceAllocator::disposeUnsafe );
    }
    
    VulkanPhysicalDevice createPhysicalDevice(
        long deviceBuffer,
        VulkanInstanceAllocator allocator,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface windowSurface
    ) {
        return register( physicalDevices, () -> new VulkanPhysicalDevice(
            this,
            allocator,
            deviceBuffer,
            instance,
            validationLayerNamesBuffer,
            windowSurface
        ) );
    }
    
    void deregisterPhysicalDevice( VulkanPhysicalDevice device ) {
        deregister( physicalDevices, device, VulkanPhysicalDevice::disposeUnsafe );
    }
    
    VulkanWindowSurface createWindowSurface( VkInstance instance, WindowContext window ) {
        return register( windowSurfaces, () -> new VulkanWindowSurface( this, instance, window ) );
    }
    
    void deregisterWindowSuface( VulkanWindowSurface surface ) {
        deregister( windowSurfaces, surface, VulkanWindowSurface::disposeUnsafe );
    }
    
    private <T> List< T > copy( List< T > list ) {
        return new ArrayList<>( list );
    }
    
    private < T > void deregister( List< T > list, T object, Consumer< T > dispose ) {
        if( list.remove( object ) ) {
            dispose.accept( object );
        }
    }
    
    private < T > T register( List< T > list, Supplier< T > create ) {
        var object = create.get();
        list.add( object );
        return object;
    }
}
