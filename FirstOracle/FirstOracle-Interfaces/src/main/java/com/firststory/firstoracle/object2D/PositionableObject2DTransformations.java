/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.PositionableObjectTransformations;
import com.firststory.firstoracle.object.data.Position2D;
import com.firststory.firstoracle.object.data.Rotation2D;
import com.firststory.firstoracle.object.data.Scale2D;

/**
 * @author n1t4chi
 */
public interface PositionableObject2DTransformations extends
    Object2DTransformations,
    PositionableObjectTransformations< Position2D, Scale2D, Rotation2D >
{
    
    default Position2D getPosition() {
        return FirstOracleConstants.POSITION_ZERO_2F;
    }
}
