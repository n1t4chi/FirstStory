/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public class IdentityTransformations3D implements ObjectTransformations3D {

    private static final IdentityTransformations3D IDENTITY_TRANSFORMATION = new IdentityTransformations3D();
    
    public static IdentityTransformations3D getIdentity() {
        return IDENTITY_TRANSFORMATION;
    }
}
