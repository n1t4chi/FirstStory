/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public class Identity2DTransformations implements PositionableObject2DTransformations {
    
    private final static Identity2DTransformations IDENTITY_TRANSFORMATION = new Identity2DTransformations();
    
    public static Identity2DTransformations getIdentity() {
        return IDENTITY_TRANSFORMATION;
    }
    
}
