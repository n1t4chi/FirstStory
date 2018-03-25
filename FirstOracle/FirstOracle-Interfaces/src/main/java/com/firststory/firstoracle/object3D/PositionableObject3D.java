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
public interface PositionableObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D, Renderer extends Object3DRenderer >
    extends Object3D< Transformations, Vertices, Renderer >
{
    
    void setTransformations( Transformations transformations );
}
