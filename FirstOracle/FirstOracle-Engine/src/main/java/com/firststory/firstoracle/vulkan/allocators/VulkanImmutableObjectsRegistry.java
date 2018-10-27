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
    private final Consumer< T > disposeAction;
    
    VulkanImmutableObjectsRegistry(
        Consumer< T > disposeAction
    ) {
        this.disposeAction = disposeAction;
    }
    
    T register( Supplier< T > supplier ) {
        return VulkanAllocatorHelper.add(
            usedInstances,
            supplier
        );
    }
    
    void deregister(
        T object
    ) {
        VulkanAllocatorHelper.disposeOnRemoval(
            usedInstances,
            object,
            disposeAction
        );
    }
    
    void dispose() {
        synchronized ( usedInstances ) {
            usedInstances.forEach( disposeAction );
            usedInstances.clear();
        }
    }
}
