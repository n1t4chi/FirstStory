/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface Object2DRenderer extends GraphicObjectRenderer< Object2D > {
    
    default void renderAll2D(
        Collection< Object2D > objects,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object2D object : objects ) {
            render( object, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    @Override
    default void render( Object2D object ) {
        render( object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    void render(
        Object2D object,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    );
    
}
