/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class CameraEvent {
    
    private final Vector2fc pos2D;
    private final Vector3fc pos3D;
    private final double rotationY;
    private final double rotationX;
    
    public CameraEvent( Vector2fc pos2D, Vector3fc pos3D, float rotationY, float rotationX ) {
        this.pos2D = new Vector2f( pos2D );
        this.pos3D = new Vector3f( pos3D );
        this.rotationY = rotationY;
        this.rotationX = rotationX;
    }
    
    public Vector2fc getPos2D() {
        return pos2D;
    }
    
    public Vector3fc getPos3D() {
        return pos3D;
    }
    
    public double getRotationY() {
        return rotationY;
    }
    
    public double getRotationX() {
        return rotationX;
    }
}
