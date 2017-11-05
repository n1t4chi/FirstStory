/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author: n1t4chi
 */
public class IdentityTransformations2D implements ObjectTransformations2D {

    private final static IdentityTransformations2D IDENTITY_TRANSFORMATION = new IdentityTransformations2D();

    public static IdentityTransformations2D getIdentity() {return IDENTITY_TRANSFORMATION;}

}
