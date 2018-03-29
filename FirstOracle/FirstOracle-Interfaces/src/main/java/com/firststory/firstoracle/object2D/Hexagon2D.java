/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public interface Hexagon2D< Transformations extends Object2DTransformations >
    extends Object2D< Transformations, Hex2DVertices >
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