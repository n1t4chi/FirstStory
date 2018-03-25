/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.rendering.Object3DRenderer;

/**
 * @author n1t4chi
 */
public interface Plane3D< Transformations extends Object3DTransformations, Renderer extends Object3DRenderer >
    extends Object3D< Transformations, Plane3DVertices, Renderer >
{
    
    Plane3DVertices VERTICES = Plane3DVertices.getPlane3DVertices();
    
    @Override
    default Plane3DVertices getVertices() {
        return VERTICES;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
