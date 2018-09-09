/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.PositionableObjectTransformations;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public interface PositionableObject2DTransformations extends
    Object2DTransformations,
    PositionableObjectTransformations< Vector2fc, Float, Vector2fc >
{
    
    default Vector2fc getPosition() {
        return FirstOracleConstants.VECTOR_ZERO_2F;
    }
}
