/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * @author n1t4chi
 */
public interface Hexagon2D< Transformations extends Object2DTransformations, Renderer extends Object2DRenderer >
    extends Object2D< Transformations, Hex2DVertices, Renderer >
{
    
    Hex2DVertices VERTICES = Hex2DVertices.getHex2DVertices();
    
    @Override
    default Hex2DVertices getVertices() {
        return VERTICES;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}