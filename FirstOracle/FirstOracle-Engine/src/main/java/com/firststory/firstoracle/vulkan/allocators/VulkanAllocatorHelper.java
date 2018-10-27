/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public interface VulkanAllocatorHelper {
    
    static <T> void safeForEach(
        Collection< T > collection,
        Consumer< T > consumer
    ) {
        List< T > copy;
        synchronized ( collection ) {
            copy = new ArrayList<>( collection );
        }
        copy.forEach( consumer );
    }
    
    static < T > void disposeOnRemoval(
        Set< T > collection,
        T object,
        Consumer< T > dispose
    ) {
        synchronized ( collection ) {
            if ( collection.remove( object ) ) {
                dispose.accept( object );
            }
        }
    }
    
    static < T > void disposeOnTransfer(
        Set< T > source,
        Deque< T > target,
        T object,
        Consumer< T > dispose
    ) {
        boolean removed;
        synchronized ( source ) {
            removed = source.remove( object );
        }
        if( removed ) {
            synchronized ( target ) {
                dispose.accept( object );
                target.add( object );
            }
        }
    }
    
    static < T > T add(
        Set< T > collection,
        Supplier< T > create
    ) {
        var object = create.get();
        synchronized ( collection ) {
            collection.add( object );
        }
        return object;
    }
    
    static < T > T transferOrNew(
        Set< T > target,
        Deque< T > source,
        Supplier< T > create
    ) {
        T object;
        synchronized ( source ) {
            object = source.poll();
        }
        object = object == null
            ? create.get()
            : object;
        synchronized ( target ) {
            target.add( object );
        }
        return object;
    }
}
