/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author n1t4chi
 */
public class BufferMap {
    private final HashMap< Long, ArrayBuffer > buffers = new HashMap<>();
    
    /**
     * Provides
     * @param key
     * @param provider
     * @return
     */
    public ArrayBuffer get( long key, AbsentArrayBufferProvider provider ){
        return buffers.computeIfAbsent( key, provider );
    }
    
    public void close() {
        buffers.forEach( ( key, arrayBuffer ) -> arrayBuffer.close() );
        buffers.clear();
    }
    
    public interface AbsentArrayBufferProvider extends Function<Long, ArrayBuffer> {
        ArrayBuffer createNew();
        
        @Override
        default ArrayBuffer apply( Long aLong ){
            return createNew();
        }
    }
}
