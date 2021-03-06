/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.PositionableObjectTransformations;

/**
 * @author n1t4chi
 */
public interface PositionableObject3DTransformations extends
    Object3DTransformations,
    PositionableObjectTransformations< Position3D, Scale3D, Rotation3D >
{
    
    @Override
    default Position3D getPosition() {
        return FirstOracleConstants.POSITION_ZERO_3F;
    }
}
