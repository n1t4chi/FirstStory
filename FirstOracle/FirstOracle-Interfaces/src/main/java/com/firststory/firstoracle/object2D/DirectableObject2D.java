/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.DirectableObject;

public interface DirectableObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends
        Object2D< Transformations, Vertices >,
        DirectableObject< Position2D, Scale2D, Rotation2D, Transformations, BoundingBox2D, Vertices >
{
}
