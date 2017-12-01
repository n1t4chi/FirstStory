/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object3D.Object3D;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface Object3DRenderer {
    
    default void renderAll3D(
        Collection< Object3D > objects,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object3D object : objects ) {
            render( object, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    default void renderAll3D(
        Collection< Object3D > objects,
        Vector3fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object3D object : objects ) {
            render( object, objectPosition, objectOverlayColour, maxAlphaChannel );
        }
    }
    
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
