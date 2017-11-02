/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera.camera3D;

import com.firststory.firstoracle.object3D.BoundingBox3D;
import com.firststory.firstoracle.object3D.Object3D;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public abstract class Camera3D {
    public boolean contains( Object3D object ) {
        BoundingBox3D bb = object.getBBO();
        return contains(
            bb.getMinX(),
            bb.getMaxX(),
            bb.getMinY(),
            bb.getMaxY(),
            bb.getMinZ(),
            bb.getMaxZ()
        );
    }

    public abstract float getAboveMaxYAlphaChannel();

    public abstract boolean contains(
        float minX, float maxX, float minY, float maxY, float minZ, float maxZ
    );

    public boolean contains( float X, float Y, float Z ) {
        return contains( X, X, Y, Y, Z, Z );
    }

    public boolean contains( Vector3fc point ) {
        return contains( point.x(), point.y(), point.z() );
    }

    public abstract Matrix4fc getMatrixRepresentation();

    public abstract Vector3fc getCenterPoint();
}
