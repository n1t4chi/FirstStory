/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.rendering.Multi2DRenderer;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface CompositeObject2D extends Object2D< Object2DTransformations, Vertices2D, Multi2DRenderer > {
    
    Terrain2D[][] getTerrains();
    Collection< Object2D > getObjects();
    
    @Override
    default void render( Multi2DRenderer renderer ) {
        renderer.renderAll( getTerrains() );
        renderer.renderAll( getObjects() );
        for ( Object2D object2D : getObjects() ) {
            renderer.render( object2D );
        }
    }
    
    default void render( Multi2DRenderer renderer, Vector4fc objectOverlayColour, float maxAlphaChannel ) {
        renderer.renderAll( getTerrains(), objectOverlayColour, maxAlphaChannel );
        renderer.renderAll( getObjects(), objectOverlayColour, maxAlphaChannel );
    }
}
