/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.Object3D;
import org.joml.Vector4fc;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface Object3DRenderer extends GraphicObjectRenderer< Object3D > {
    
    default void renderAll3D(
        Collection< Object3D > objects,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    ) {
        for ( Object3D object : objects ) {
            render( object, objectOverlayColour, maxAlphaChannel );
        }
    }
    
    @Override
    default void render( Object3D object ) {
        render( object, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    void render(
        Object3D object,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    );
}
