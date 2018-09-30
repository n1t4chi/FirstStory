/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author n1t4chi
 */
public interface Object3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends GraphicObject< Transformations, BoundingBox3D, Vertices >
{
    
    @Override
    Transformations getTransformations();
    
    @Override
    Vertices getVertices();
    
    @Override
    default BoundingBox3D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations(), FirstOracleConstants.POSITION_ZERO_3F );
    }
}
