/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author n1t4chi
 */
public interface Object2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends GraphicObject< Transformations, BoundingBox2D, Vertices >
{
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations(), FirstOracleConstants.POSITION_ZERO_2F );
    }
    
    @Override
    Transformations getTransformations();
    
    @Override
    Vertices getVertices();
}
