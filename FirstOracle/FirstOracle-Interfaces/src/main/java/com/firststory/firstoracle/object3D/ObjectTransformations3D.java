/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.ObjectTransformations;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public interface ObjectTransformations3D extends ObjectTransformations< Vector3fc, Vector3fc, Vector3fc > {
    Vector3fc ZERO = new Vector3f( 0, 0, 0 );
    Vector3fc ONE = new Vector3f( 1, 1, 1 );
    
    @Override
    default Vector3fc getScale() {
        return ONE;
    }
    
    @Override
    default Vector3fc getRotation() {
        return ZERO;
    }
    
    @Override
    default Vector3fc getPosition() {
        return ZERO;
    }
    
}
