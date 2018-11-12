/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.PositionableObject;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface PositionableObject3D<
    Transformations extends PositionableObject3DTransformations,
    Vertices extends Vertices3D
> extends
    Object3D< Transformations, Vertices >,
    PositionableObject< Transformations, BoundingBox3D, Vertices >
{
    
    @Override
    default BoundingBox3D getBBO() {
        return getVertices()
            .getBoundingBox()
            .getTransformedBoundingBox(
                getTransformations(),
                getTransformations().getPosition()
            );
    }
    
}
