/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public class IdentityCamera2D extends Camera2D {

    private static final IdentityCamera2D CAMERA = new IdentityCamera2D();

    public static IdentityCamera2D getCamera() {
        return CAMERA;
    }

    private static final Matrix3f IDENTITY_MATRIX = new Matrix3f();
    private static final Vector2f POINT = new Vector2f();

    @Override
    public Matrix3fc getMatrixRepresentation() { return IDENTITY_MATRIX; }

    @Override
    public Vector2fc getCenterPoint() { return POINT; }

    @Override
    public boolean contains( float minX, float maxX, float minY, float maxY ) {
        return minX <= 1 && maxX >= -1 && minY <= 1 && maxY >= -1;
    }

}
