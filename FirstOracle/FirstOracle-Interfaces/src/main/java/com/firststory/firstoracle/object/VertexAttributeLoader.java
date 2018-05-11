/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.ArrayBuffer;

/**
 * @author n1t4chi
 */
public interface VertexAttributeLoader<VertexBuffer extends ArrayBuffer> {
    VertexBuffer createEmptyBuffer();
    void bindBuffer( VertexAttribute attribute, long key );
}
