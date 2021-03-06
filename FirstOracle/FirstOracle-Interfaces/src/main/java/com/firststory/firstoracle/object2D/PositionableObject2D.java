/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object.PositionableObject;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface PositionableObject2D<
    Transformations extends PositionableObject2DTransformations,
    Vertices extends Vertices2D
> extends
    Object2D< Transformations, Vertices >,
    PositionableObject< Position2D, Scale2D, Rotation2D, Transformations, BoundingBox2D, Vertices >
{
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices()
            .getBoundingBox()
            .getTransformedBoundingBox(
                getTransformations(),
                getTransformations().getPosition()
            );
    }
}