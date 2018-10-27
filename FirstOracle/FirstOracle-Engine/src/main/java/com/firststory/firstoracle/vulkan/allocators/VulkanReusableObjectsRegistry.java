/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
class VulkanReusableObjectsRegistry< T > {
    
    private final Deque< T > freeInstances = new LinkedList<>();
    private final Set< T > usedInstances = new HashSet<>();
    
    T register(
        Supplier< T > supplier,
        Consumer< T > update
    ) {
        var
            object =
            VulkanAllocatorHelper.transferOrNew( usedInstances,
                freeInstances,
                supplier
            );
        update.accept( object );
        return object;
    }
    
    void deregister(
        T object,
        Consumer< T > dispose
    ) {
        VulkanAllocatorHelper.disposeOnTransfer(
            usedInstances,
            freeInstances,
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
