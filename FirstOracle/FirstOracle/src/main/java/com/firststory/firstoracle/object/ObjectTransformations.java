/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author: n1t4chi
 */
public interface ObjectTransformations {
    ObjectTransformations IDENTITY_TRANSFORMATION = new IdentityTransformations();
    Vector3fc ZERO = new Vector3f( 0, 0, 0 );
    Vector3fc ONE = new Vector3f( 1, 1, 1 );

    static ObjectTransformations getIdentity() {return IDENTITY_TRANSFORMATION;}
    
    default Vector3fc getScale() {
        return ONE;
    }
    
    default Vector3fc getRotation() {
        return ZERO;
    }
    
    default Vector3fc getPosition() {
        return ZERO;
    }
    
    class IdentityTransformations implements ObjectTransformations {}
}
