/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.MutableTransformationsObject;

/**
 * @author n1t4chi
 */
public interface MutableTransformationsObject3D< VerticesType extends Vertices3D > extends MutableTransformationsObject< 
    Position3D, 
    Scale3D, 
    Rotation3D,
    MutablePositionable3DTransformations,
    BoundingBox3D,
    VerticesType
> {}
