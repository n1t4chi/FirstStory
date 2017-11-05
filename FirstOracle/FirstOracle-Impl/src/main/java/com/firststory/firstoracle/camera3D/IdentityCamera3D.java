/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class IdentityCamera3D extends Camera3D {
    
    private static final IdentityCamera3D CAMERA = new IdentityCamera3D();

    public static IdentityCamera3D getCamera() {
        return CAMERA;
    }
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    private static final Vector3f POINT = new Vector3f( 0, 0, 0 );
    
    @Override
    public Vector3fc getCenterPoint() { return POINT; }
    
    @Override
    public float getAboveMaxYAlphaChannel() { return 0; }
    
    @Override
    public Matrix4fc getMatrixRepresentation() { return IDENTITY_MATRIX; }
    
    @Override
    public boolean contains(
                               float minX,
                               float maxX,
                               float minY,
                               float maxY,
                               float minZ,
                               float maxZ
    ) {
        return minX <= 1 && maxX >= -1 && minY <= 1 && maxY >= -1;
    }
    
}
