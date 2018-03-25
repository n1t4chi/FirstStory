/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * @author n1t4chi
 */
public interface Object2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D, Renderer extends Object2DRenderer >
    extends GraphicObject< Transformations, BoundingBox2D, Vertices, Renderer >
{
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
    
    @Override
    Transformations getTransformations();
    
    @Override
    Vertices getVertices();
    
    @Override
    default void render( Renderer renderer ) {
        renderer.render( this );
    }
}
