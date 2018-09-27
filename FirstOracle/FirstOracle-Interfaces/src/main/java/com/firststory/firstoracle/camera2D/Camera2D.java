/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.object2D.Object2D;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public abstract class Camera2D implements Camera {
    
    private boolean update = true;
    
    public abstract Vector2fc getCenterPoint();
    
    public boolean contains( Vector2fc point ) {
        return contains( point.x(), point.y() );
    }
    
    public boolean contains( float X, float Y ) {
        return contains( X, X, Y, Y );
    }
    
    public abstract boolean contains( float minX, float maxX, float minY, float maxY );
    
    public boolean contains( Object2D< ?, ? > object ) {
        var bb = object.getBBO();
        return contains( bb.getMinX(), bb.getMaxX(), bb.getMinY(), bb.getMaxY() );
    }
    
    public abstract Vector2fc translatePointOnScreen( float x, float y, int width, int height );
    
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
