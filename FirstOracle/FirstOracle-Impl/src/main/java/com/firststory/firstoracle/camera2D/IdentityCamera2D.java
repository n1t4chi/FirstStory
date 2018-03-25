/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public class IdentityCamera2D extends Camera2D {
    
    private static final IdentityCamera2D CAMERA = new IdentityCamera2D();
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    private static final Vector2f POINT = new Vector2f();
    
    public static IdentityCamera2D getCamera() {
        return CAMERA;
    }
    
    @Override
    public Matrix4fc getMatrixRepresentation() {
        return IDENTITY_MATRIX;
    }
    
    @Override
    public Vector2fc getCenterPoint() {
        return POINT;
    }
    
    @Override
    public boolean contains( float minX, float maxX, float minY, float maxY ) {
        return minX <= 1 && maxX >= -1 && minY <= 1 && maxY >= -1;
    }
    
    @Override
    public Vector2fc translatePointOnScreen( float x, float y, int width, int height ) {
        return new Vector2f( 2f * x / width - 1f, -( 2f * y / height - 1f ) );
    }
    
    @Override
    public float getGeneralRotation() {
        return 0;
    }
}
