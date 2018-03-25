/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.rendering.Object3DRenderer;

/**
 * @author n1t4chi
 */
public interface Object3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D, Renderer extends Object3DRenderer >
    extends GraphicObject< Transformations, BoundingBox3D, Vertices, Renderer >
{
    
    @Override
    Transformations getTransformations();
    
    @Override
    Vertices getVertices();
    
    @Override
    default BoundingBox3D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
    
    @Override
    default void render( Renderer renderer ) {
        renderer.render( this );
    }
}
