/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
class VulkanImmutableObjectsRegistry< T > {
    
    private final Set< T > usedInstances = new HashSet<>();
    
    T register( Supplier< T > supplier ) {
        return VulkanAllocatorHelper.add(
            usedInstances,
            supplier
        );
    }
    
    void deregister(
        T object,
        Consumer< T > dispose
    ) {
        VulkanAllocatorHelper.disposeOnRemoval(
            usedInstances,
            object,
            dispose
        );
    }
    
    void forEach( Consumer< T > consumer ) {
        VulkanAllocatorHelper.safeForEach(
            usedInstances,
            consumer
        );
    }
}
