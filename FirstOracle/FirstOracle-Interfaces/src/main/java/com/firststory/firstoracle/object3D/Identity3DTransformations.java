/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public class Identity3DTransformations implements Object3DTransformations {
    
    private static final Identity3DTransformations IDENTITY_TRANSFORMATION = new Identity3DTransformations();
    
    public static Identity3DTransformations getIdentity() {
        return IDENTITY_TRANSFORMATION;
    }
}
