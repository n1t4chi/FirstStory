/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.NonDirectableObject;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface NonDirectableObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends
        Object2D< Transformations, Vertices >,
        NonDirectableObject< Position2D, Scale2D, Rotation2D, Transformations, BoundingBox2D, Vertices >
{
}