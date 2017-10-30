/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import org.joml.*;

/**
 * @author n1t4chi
 */
public class IdentityCamera extends Camera {
    
    private static final IdentityCamera CAMERA = new IdentityCamera();
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
