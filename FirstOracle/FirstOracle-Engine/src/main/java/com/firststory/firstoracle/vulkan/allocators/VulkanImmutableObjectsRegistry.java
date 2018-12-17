/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import java.util.*;
import java.util.function.*;

/**
 * @author n1t4chi
 */
class VulkanImmutableObjectsRegistry< T > implements VulkanRegistry {
    
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
    
    @Override
    public void dispose() {
        synchronized ( usedInstances ) {
            usedInstances.forEach( disposeAction );
            usedInstances.clear();
        }
    }
}
