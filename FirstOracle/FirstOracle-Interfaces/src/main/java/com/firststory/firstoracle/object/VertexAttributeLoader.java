/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.buffer.DataBuffer;

/**
 * @author n1t4chi
 */
public interface VertexAttributeLoader< VertexBuffer extends DataBuffer< ? > > {
    
    VertexBuffer provideBuffer( float[] array );
    
    default VertexBuffer extractVerticesBuffer( Vertices<?, ?> vertices, int frame ) {
        return vertices.getBuffer( this, frame );
    }
    
    default VertexBuffer extractUvMapBuffer( UvMap uvMap, int direction, int frame ) {
        return uvMap.getBuffer( this, direction, frame );
    }
    
    default VertexBuffer extractColouringBuffer( Colouring colouring ) {
        return colouring.getBuffer( this );
    }
}
