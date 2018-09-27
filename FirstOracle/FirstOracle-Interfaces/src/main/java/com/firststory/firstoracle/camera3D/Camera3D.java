/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.object3D.Object3D;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public abstract class Camera3D implements Camera {
    
    private boolean update = true;
    
    public boolean contains( Object3D< ?, ? > object ) {
        var bb = object.getBBO();
        return contains( bb.getMinX(),
            bb.getMaxX(),
            bb.getMinY(),
            bb.getMaxY(),
            bb.getMinZ(),
            bb.getMaxZ()
        );
    }
    
    public abstract boolean contains(
        float minX, float maxX, float minY, float maxY, float minZ, float maxZ
    );
    
    public abstract float getAboveMaxYAlphaChannel();
    
    public boolean contains( Vector3fc point ) {
        return contains( point.x(), point.y(), point.z() );
    }
    
    public boolean contains( float X, float Y, float Z ) {
        return contains( X, X, Y, Y, Z, Z );
    }
    
    public abstract Vector3fc getCenterPoint();
    
    public boolean mustUpdate() {
        return update;
    }
    
    public void forceUpdate() {
        update = true;
    }
    
    protected void updated() {
        update = false;
    }
    
    /**
     * Returns general rotation of this camera for objects to determine facing position
     *
     * @return rotation
     */
    public abstract float getGeneralRotation();
}
