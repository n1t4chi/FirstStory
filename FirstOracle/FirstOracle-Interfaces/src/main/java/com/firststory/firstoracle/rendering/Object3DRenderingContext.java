/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.*;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public interface Object3DRenderingContext extends ObjectRenderingContext<
    Vector3fc,
    Vector3fc,
    Vector3fc,
    Object3DTransformations,
    Vertices3D,
    Object3D< ? extends Object3DTransformations, ? extends Vertices3D >
    > {
    
    
    default void render(
        PositionableObject3D< ? extends PositionableObject3DTransformations, ? extends Vertices3D > object,
        double timeSnapshot,
        double cameraRotation
    ) {
        renderVerticesAsTriangles(
            object.getVertices(),
            0,
            object.getUvMap(),
            0,
            0,
            object.getColours(),
            FirstOracleConstants.VECTOR_ONES_3F,
            FirstOracleConstants.VECTOR_ONES_3F,
            FirstOracleConstants.VECTOR_ONES_3F,
            object.getTexture(),
            FirstOracleConstants.RED,
            1f
        );
//        render(
//            object,
//            object.getTransformations().getPosition(),
//            timeSnapshot,
//            cameraRotation
//        );
    }
    
}
