/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.rendering.Object3DRenderer;

/**
 * @author n1t4chi
 */
public interface Cube< Transformations extends Object3DTransformations, Renderer extends Object3DRenderer >
    extends Object3D< Transformations, CubeVertices, Renderer >
{
    
    CubeVertices VERTICES = CubeVertices.getCubeVertices();
    
    @Override
    default CubeVertices getVertices() {
        return VERTICES;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
