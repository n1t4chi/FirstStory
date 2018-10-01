/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object.ObjectTransformations;

/**
 * @author n1t4chi
 */
public interface Object2DTransformations extends ObjectTransformations< Scale2D, Rotation2D > {
    
    @Override
    default Scale2D getScale() {
        return FirstOracleConstants.SCALE_ONE_2F;
    }
    
    @Override
    default Rotation2D getRotation() {
        return FirstOracleConstants.ROTATION_ZERO_2F;
    }
}
