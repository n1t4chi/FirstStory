/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.MutableTransformationsObject;

/**
 * @author n1t4chi
 */
public interface MutableTransformationsObject2D< VerticesType extends Vertices2D >
    extends MutableTransformationsObject<
        Position2D,
        Scale2D,
        Rotation2D,
        MutablePositionable2DTransformations,
        BoundingBox2D,
        VerticesType
    >
{}
