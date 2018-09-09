/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.PositionableObjectTransformations;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public interface PositionableObject3DTransformations extends
    Object3DTransformations,
    PositionableObjectTransformations< Vector3fc, Vector3fc, Vector3fc>
{
    
    @Override
    default Vector3fc getPosition() {
        return FirstOracleConstants.VECTOR_ZERO_3F;
    }
}
