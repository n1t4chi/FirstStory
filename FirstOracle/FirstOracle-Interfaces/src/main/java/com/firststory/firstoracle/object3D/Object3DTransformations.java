/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.ObjectTransformations;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public interface Object3DTransformations extends ObjectTransformations< Vector3fc, Vector3fc > {
    
    @Override
    default Vector3fc getScale() {
        return FirstOracleConstants.VECTOR_ONES_3F;
    }
    
    @Override
    default Vector3fc getRotation() {
        return FirstOracleConstants.VECTOR_ZERO_3F;
    }
}
