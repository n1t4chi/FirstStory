/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object3D.*;

/**
 * @author n1t4chi
 */
public interface Object3DRenderingContext extends ObjectRenderingContext<
    Position3D,
    Scale3D,
    Rotation3D,
    Object3DTransformations,
    Vertices3D,
    Object3D< ? extends Object3DTransformations, ? extends Vertices3D >
> {
    
    
    default void render(
        PositionableObject3D< ? extends PositionableObject3DTransformations, ? extends Vertices3D > object,
        double timeSnapshot,
        double cameraRotation
    ) {
        renderObject(
            object,
            object.getTransformations().getPosition(),
            timeSnapshot,
            cameraRotation
        );
    }
    
}
