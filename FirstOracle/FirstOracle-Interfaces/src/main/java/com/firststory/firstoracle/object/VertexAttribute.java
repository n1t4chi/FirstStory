/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.BufferMap;
import com.firststory.firstoracle.data.DataBuffer;
import com.firststory.firstoracle.object.data.FloatData;

import java.util.HashMap;
import java.util.List;

/**
 * @author n1t4chi
 */
public abstract class VertexAttribute< Data extends FloatData > {
    
    private final HashMap< VertexAttributeLoader< ? >, BufferMap > bufferMaps = new HashMap<>();
    
    @SuppressWarnings( "unchecked" )
    public < VertexBuffer extends DataBuffer< ? > > VertexBuffer getBuffer(
        VertexAttributeLoader< VertexBuffer > loader,
        int... parameters
    ) {
        var key = getKey( parameters );
        return ( VertexBuffer ) bufferMaps.computeIfAbsent( loader, ignored -> new BufferMap() )
            .get( key, () -> loader.provideBuffer( floatDataToArray( getData( parameters ) ) ) );
    }
    
    public void dispose() {
        bufferMaps.forEach( ( loader, bufferMap ) -> bufferMap.close() );
        bufferMaps.clear();
    }
    
    protected abstract List< Data > getData( int... parameters );
    
    public int getVertexLength( int frame ) {
        return getData( frame ).size();
    }
    
    protected abstract long getKey( int... parameters );
    
    private float[] floatDataToArray( List< ? extends FloatData > vertexData ) {
        if ( vertexData.isEmpty() ) {
            return FirstOracleConstants.EMPTY_FLOAT_ARRAY;
        }
        var size = vertexData.get( 0 ).size();
        var array = new float[ vertexData.size() * size ];
    
        var iterator = 0;
        
        for ( FloatData data : vertexData ) {
            System.arraycopy( data.data(), 0, array, iterator, size );
            iterator += size;
        }
        return array;
    }
}
