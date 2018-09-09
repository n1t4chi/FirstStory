/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object2D.*;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public interface Object2DRenderingContext extends ObjectRenderingContext<
    Vector2fc,
    Vector2fc,
    Float,
    Object2DTransformations,
    Vertices2D,
    Object2D< ? extends Object2DTransformations, ? extends Vertices2D >
> {
    
    
    default void render(
        PositionableObject2D< ? extends PositionableObject2DTransformations, ? extends Vertices2D > object,
        double timeSnapshot,
        double cameraRotation
    ) {
        render(
            object,
            object.getTransformations().getPosition(),
            timeSnapshot,
            cameraRotation
        );
    }

}
