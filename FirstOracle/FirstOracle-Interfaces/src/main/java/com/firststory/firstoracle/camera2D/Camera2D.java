/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import com.firststory.firstoracle.object2D.BoundingBox2D;
import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Matrix3fc;
import org.joml.Vector2fc;

/**
 * @author: n1t4chi
 */
public abstract class Camera2D {

    public abstract Matrix3fc getMatrixRepresentation();

    public abstract Vector2fc getCenterPoint();

    public abstract boolean contains( float minX, float maxX, float minY, float maxY );

    public boolean contains( float X, float Y ) {
        return contains( X, X, Y, Y );
    }

    public boolean contains( Vector2fc point ) {
        return contains( point.x(), point.y() );
    }

    public boolean contains( Object2D object ) {
        BoundingBox2D bb = object.getBBO();
        return contains( bb.getMinX(), bb.getMaxX(), bb.getMinY(), bb.getMaxY() );
    }
}
