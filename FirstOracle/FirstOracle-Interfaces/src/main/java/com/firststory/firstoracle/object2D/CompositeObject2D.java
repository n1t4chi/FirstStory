/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.rendering.Object2DRenderer;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface CompositeObject2D extends Object2D {
    
    Collection< Object2D > getObjects();
    
    @Override
    default void render( Object2DRenderer renderer ) {
        for ( Object2D object2D : getObjects() ) {
            renderer.render( object2D );
        }
    }
    
    default void render( Object2DRenderer renderer, Vector4fc objectOverlayColour, float maxAlphaChannel ) {
        for ( Object2D object2D : getObjects() ) {
            renderer.render( object2D, objectOverlayColour, maxAlphaChannel );
        }
    }
}
