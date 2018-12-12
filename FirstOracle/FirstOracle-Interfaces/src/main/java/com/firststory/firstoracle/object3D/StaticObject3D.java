/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.StaticObject;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface StaticObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends
        Object3D< Transformations, Vertices >,
        StaticObject< Position3D, Scale3D, Rotation3D, Transformations, BoundingBox3D, Vertices >
{
}
