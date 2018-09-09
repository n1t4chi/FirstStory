/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.ObjectTransformations;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public interface Object2DTransformations extends ObjectTransformations< Vector2fc, Float > {
    
    @Override
    default Vector2fc getScale() {
        return FirstOracleConstants.VECTOR_ONES_2F;
    }
    
    @Override
    default Float getRotation() {
        return 0f;
    }
}
