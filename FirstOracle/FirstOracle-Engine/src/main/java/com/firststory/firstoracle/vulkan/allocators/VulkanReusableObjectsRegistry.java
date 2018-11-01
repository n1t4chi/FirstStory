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
    private final Consumer< T > disposeUsedAction;
    private final Consumer< T > deregisterAction;
    private final Consumer< T > disposeFreeAction;
    
    VulkanReusableObjectsRegistry(
        Consumer< T > disposeUsedAction
    ) {
        this( t -> {}, disposeUsedAction );
    }
    
    VulkanReusableObjectsRegistry(
        Consumer< T > disposeFreeAction,
        Consumer< T > disposeUsedAction
    ) {
        this( disposeUsedAction, disposeFreeAction, disposeUsedAction );
    }
    
    VulkanReusableObjectsRegistry(
        Consumer< T > deregisterAction,
        Consumer< T > disposeFreeAction,
        Consumer< T > disposeUsedAction
    ) {
        this.deregisterAction = deregisterAction;
        this.disposeFreeAction = disposeFreeAction;
        this.disposeUsedAction = disposeUsedAction;
    }
    
    T register(
        Supplier< T > supplier,
        Consumer< T > update
    ) {
        var object = VulkanAllocatorHelper.transferOrNew( usedInstances,
            freeInstances,
            supplier
        );
        update.accept( object );
        return object;
    }
    
    void deregister(
        T object
    ) {
        VulkanAllocatorHelper.disposeOnTransfer(
            usedInstances,
            freeInstances,
            object,
            deregisterAction
        );
    }
    
    void executeOnEach( Consumer< T > action ) {
        synchronized ( usedInstances ) {
            usedInstances.forEach( action );
        }
        synchronized ( freeInstances ) {
            freeInstances.forEach( action );
        }
    }
    
    void dispose() {
        synchronized ( usedInstances ) {
            usedInstances.forEach( disposeUsedAction );
            usedInstances.clear();
        }
        synchronized ( freeInstances ) {
            freeInstances.forEach( disposeFreeAction );
            freeInstances.clear();
        }
    }
}
