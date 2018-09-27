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
    
    private final HashMap< Long, DataBuffer< ? > > buffers = new HashMap<>();
    
    public DataBuffer< ? > get( long key, AbsentArrayBufferProvider provider ) {
        return buffers.computeIfAbsent( key, provider );
    }
    
    public void close() {
        buffers.forEach( ( key, arrayBuffer ) -> arrayBuffer.close() );
        buffers.clear();
    }
    
    public interface AbsentArrayBufferProvider extends Function< Long, DataBuffer< ? > > {
        
        DataBuffer< ? > createNew();
        
        @Override
        default DataBuffer< ? > apply( Long aLong ) {
            return createNew();
        }
    }
}
