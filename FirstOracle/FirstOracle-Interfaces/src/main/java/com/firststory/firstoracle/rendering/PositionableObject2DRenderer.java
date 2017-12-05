/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface PositionableObject2DRenderer< Object extends Object2D > extends Object2DRenderer< Object > {
    
    default void renderAll( Collection< Object > objects, Vector4fc objectOverlayColour, float maxAlphaChannel ) {
        for ( Object object : objects ) {
            renderObject( object, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    default void renderAll( Collection< Object > objects ) {
        renderAll( objects, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    @Override
    default void render( Object object ) {
        renderObject( object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    default void renderObject( Object object, Vector4fc objectOverlayColour, float maxAlphaChannel ){
        renderObject( object,object.getTransformations().getPosition(),objectOverlayColour,maxAlphaChannel );
    }
    
    void renderObject( Object object, Vector2fc objectPosition,Vector4fc objectOverlayColour, float maxAlphaChannel );
    
}
