/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.DataBuffer;

/**
 * @author n1t4chi
 */
public interface VertexAttributeLoader< VertexBuffer extends DataBuffer > {
    
    VertexBuffer provideBuffer( float[] array );
    
    void bindBuffer( VertexAttribute attribute, long key );
}
