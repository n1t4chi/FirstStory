/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.rendering.Object3DRenderer;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Object3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends GraphicObject< Transformations, BoundingBox3D, Vertices, Object3DRenderer >
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
    default void render( Object3DRenderer renderer ) {
        renderer.render( this );
    }
    
    default void render( Object3DRenderer renderer, Vector4fc objectOverlayColour, float maxAlphaChannel ) {
        renderer.render( this, objectOverlayColour, maxAlphaChannel );
    }
}
