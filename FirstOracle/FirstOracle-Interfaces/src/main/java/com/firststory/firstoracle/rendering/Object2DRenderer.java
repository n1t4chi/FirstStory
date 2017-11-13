/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Object2DRenderer {

    default void render(
        Object2D object,
        Vector2fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ){}

    default void render(
        Object2D object,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    )
    {
        render( object,
            object.getTransformations().getPosition(),
            objectOverlayColour,
            maxAlphaChannel );
    }
}
