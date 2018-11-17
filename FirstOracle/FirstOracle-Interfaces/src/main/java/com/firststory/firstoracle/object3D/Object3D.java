/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author n1t4chi
 */
public interface Object3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends GraphicObject< Position3D, Scale3D, Rotation3D, Transformations, BoundingBox3D, Vertices >
{
    @Override
    default BoundingBox3D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations(), FirstOracleConstants.POSITION_ZERO_3F );
    }
}
