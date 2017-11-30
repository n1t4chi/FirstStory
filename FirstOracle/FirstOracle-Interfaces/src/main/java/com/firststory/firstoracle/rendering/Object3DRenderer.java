/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object3D.Object3D;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Object3DRenderer {
    
    default void render(
        Object3D object,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        render( object,
            object.getTransformations().getPosition(),
            objectOverlayColour,
            maxAlphaChannel );
    }
    
    default void render(
        Object3D object,
        Vector3fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
    }
}
