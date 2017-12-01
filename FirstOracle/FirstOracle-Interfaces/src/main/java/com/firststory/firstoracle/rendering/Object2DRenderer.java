/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface Object2DRenderer {
    
    default void renderAll2D(
        Collection< Object2D > objects,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object2D object : objects ) {
            render( object, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    default void renderAll2D(
        Collection< Object2D > objects,
        Vector2fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object2D object : objects ) {
            render( object, objectPosition, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    default void render(
        Object2D object,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        render( object,
            object.getTransformations().getPosition(),
            objectOverlayColour,
            maxAlphaChannel );
    }
    
    default void render(
        Object2D object,
        Vector2fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
    }
}
