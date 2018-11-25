/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera3D.Camera3D;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
class DummyCamera3D extends Camera3D {
    
    private final Matrix4f camera;
    
    DummyCamera3D( Matrix4f camera ) {
        this.camera = camera;
    }
    
    @Override
    public boolean contains(
        float minX,
        float maxX,
        float minY,
        float maxY,
        float minZ,
        float maxZ
    ) {
        return true;
    }
    
    @Override
    public float getAboveMaxYAlphaChannel() {
        return 1;
    }
    
    @Override
    public Vector3fc getCenterPoint() {
        return FirstOracleConstants.VECTOR_ZERO_3F;
    }
    
    @Override
    public float getGeneralRotation() {
        return 0;
    }
    
    @Override
    public Matrix4fc getMatrixRepresentation() {
        return camera;
    }
}
