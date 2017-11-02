/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera.camera2D;

import org.joml.*;

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
        float width,
        float X,
        float Y,
        float heightByWidthRatio,
        float rotation
    )
    {
        this.position = new Vector2f( X, Y );
        this.rotation = rotation;
        camera = new Matrix3f();
        update = false;
        this.width = width;
        this.heightByWidthRatio = heightByWidthRatio;
    }

    @Override
    public Matrix3fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }

    @Override
    public Vector2fc getCenterPoint() {
        return null;
    }

    @Override
    public boolean contains( float minX, float maxX, float minY, float maxY ) { return true; }

    private void updateMatrix() {
        if ( !update ) {
            camera.identity();
            camera.scale( width , width*heightByWidthRatio , 1 );
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
