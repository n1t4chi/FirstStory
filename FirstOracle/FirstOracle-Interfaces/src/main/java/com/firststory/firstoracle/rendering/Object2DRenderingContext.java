/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object2D.*;

/**
 * @author n1t4chi
 */
public interface Object2DRenderingContext extends ObjectRenderingContext<
    Position2D,
    Scale2D,
    Rotation2D,
    Object2DTransformations,
    Vertices2D,
    Object2D< ? extends Object2DTransformations, ? extends Vertices2D >
> {
    
    
    default void render(
        PositionableObject2D< ? extends PositionableObject2DTransformations, ? extends Vertices2D > object,
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
