/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * @author n1t4chi
 */
public interface Rectangle< Transformations extends Object2DTransformations, Renderer extends Object2DRenderer >
    extends Object2D< Transformations, Plane2DVertices, Renderer >
{
    
    Plane2DVertices VERTICES = Plane2DVertices.getPlane2DVertices();
    
    @Override
    default Plane2DVertices getVertices() {
        return VERTICES;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}