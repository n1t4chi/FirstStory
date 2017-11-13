/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author: n1t4chi
 */
public class MovableCamera2D extends Camera2D {

    private final Vector2f position;
    private final Matrix3f camera;
    private boolean update;
    private float width;
    private float heightByWidthRatio;
    private float rotation;

    public MovableCamera2D(
        float width, float X, float Y, float heightByWidthRatio, float rotation
    )
    {
        this.position = new Vector2f( X, Y );
        this.rotation = rotation;
        camera = new Matrix3f();
        update = false;
        this.width = width;
        this.heightByWidthRatio = heightByWidthRatio;
    }

    public void setRotation( float rotation ) {
        if ( this.rotation != rotation ) {
            update = true;
            this.rotation = rotation;
        }
    }

    public void setWidth( float width ) {
        if ( this.width != width ) {
            update = true;
            this.width = width;
        }
    }

    public void setHeightByWidthRatio( float ratio ) {
        if ( heightByWidthRatio != ratio ) {
            update = true;
            this.heightByWidthRatio = ratio;
        }
    }

    public final void setCenterPoint( float X, float Y ) {
        if ( position.x != X || position.y != Y ) {
            update = true;
            this.position.set( X, Y );
        }
    }

    @Override
    public Matrix3fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }

    @Override
    public Vector2fc getCenterPoint() {
        return position;
    }

    @Override
    public boolean contains( float minX, float maxX, float minY, float maxY ) { return true; }

    private void updateMatrix() {
        if ( !update ) {
            camera.identity();
            camera.scale( width, width * heightByWidthRatio, 1 );
            translate();
            camera.rotateX( ( float ) java.lang.Math.toRadians( rotation ) );
            update = false;
        }
    }

    private void translate() {
        camera.m02 = position.x;
        camera.m12 = position.y;
    }
}
