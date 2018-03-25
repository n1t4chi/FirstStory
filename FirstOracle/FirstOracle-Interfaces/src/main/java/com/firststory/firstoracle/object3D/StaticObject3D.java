/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.rendering.Object3DRenderer;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface StaticObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D, Renderer extends Object3DRenderer >
    extends Object3D< Transformations, Vertices, Renderer >
{
    
    @Override
    default int getCurrentUvMapDirection( double currentCameraRotation ) {
        return 0;
    }
    
    @Override
    default int getCurrentUvMapFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
