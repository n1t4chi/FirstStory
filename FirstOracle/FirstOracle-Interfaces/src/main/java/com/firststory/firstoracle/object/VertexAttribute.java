/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.BufferMap;
import com.firststory.firstoracle.data.DataBuffer;

import java.util.HashMap;

/**
 * @author n1t4chi
 */
public abstract class VertexAttribute {
    private final HashMap< VertexAttributeLoader, BufferMap > bufferMaps = new HashMap<>();
    private final HashMap< Long, float[] > arrays = new HashMap<>(  );
    
    
    public DataBuffer getBuffer( long key, VertexAttributeLoader loader ) {
        return bufferMaps.computeIfAbsent( loader, ignored -> new BufferMap() )
            .get(key, () -> loader.provideBuffer( getArray( key ) ) )
        ;
    }
    
    public void close() {
        bufferMaps.forEach( ( loader, bufferMap ) -> bufferMap.close() );
        bufferMaps.clear();
    }
    
    public abstract int getIndex();
    
    public abstract int getVertexSize();
    
    public abstract float[] getArray( long key );
    
    int getVertexLength( long key ) {
        return getArray( key ).length / getVertexSize();
    }
}
