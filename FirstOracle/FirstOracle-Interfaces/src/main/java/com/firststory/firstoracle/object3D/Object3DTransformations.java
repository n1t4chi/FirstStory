/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.ObjectTransformations;

/**
 * @author n1t4chi
 */
public interface Object3DTransformations extends ObjectTransformations< Scale3D, Rotation3D > {
    
    @Override
    default Scale3D getScale() {
        return FirstOracleConstants.SCALE_ONE_3F;
    }
    
    @Override
    default Rotation3D getRotation() {
        return FirstOracleConstants.ROTATION_ZERO_3F;
    }
}
