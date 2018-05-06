/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.ArrayBuffer;
import com.firststory.firstoracle.data.BufferMap;

import java.util.HashMap;

/**
 * @author n1t4chi
 */
public abstract class VertexAttribute<Context> implements AutoCloseable {
    private final HashMap< VertexAttributeLoader<Context>, BufferMap > bufferMaps = new HashMap<>();
    private final HashMap< Long, float[] > arrays = new HashMap<>(  );
    
    
    public ArrayBuffer<Context> getBuffer( long key, VertexAttributeLoader<Context> loader ) {
        return bufferMaps.computeIfAbsent( loader, ignored -> new BufferMap() )
            .get(key, () -> {
                ArrayBuffer<Context> newBuffer = loader.createEmptyBuffer();
                newBuffer.create();
                newBuffer.load( getArray( key ) );
                return newBuffer;
            } )
        ;
    }
    
    @Override
    public void close() {
        bufferMaps.forEach( ( loader, bufferMap ) -> bufferMap.close() );
        bufferMaps.clear();
    }
    
    public abstract int getIndex();
    
    public abstract int getVertexSize();
    
    public int getVertexLength( ArrayBuffer buffer ) {
        return buffer.getLength() / getVertexSize();
    }
    
    public abstract float[] getArray( long key );
    
    public int getVertexLength( long key ) {
        return getArray( key ).length / getVertexSize();
    }
}
