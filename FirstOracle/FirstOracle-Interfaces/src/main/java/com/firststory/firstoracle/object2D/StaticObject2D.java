/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface StaticObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D, Renderer extends Object2DRenderer >
    extends Object2D< Transformations, Vertices, Renderer >
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
