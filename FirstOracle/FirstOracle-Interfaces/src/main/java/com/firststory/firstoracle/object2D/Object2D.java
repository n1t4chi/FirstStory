/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.rendering.Object2DRenderer;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Object2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends GraphicObject< Transformations, BoundingBox2D, Vertices, Object2DRenderer >
{
    
    @Override
    Transformations getTransformations();
    
    @Override
    Vertices getVertices();
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
    
    @Override
    default void render( Object2DRenderer renderer ) {
        renderer.render( this );
    }
    
    default void render( Object2DRenderer renderer, Vector4fc objectOverlayColour, float maxAlphaChannel ) {
        renderer.render( this, objectOverlayColour, maxAlphaChannel );
    }
}
