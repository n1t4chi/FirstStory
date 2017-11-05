/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class IsometricCamera3D extends Camera3D {

    private final float heightByWidthRatio;
    private final float aboveYAlphaOverride;
    private final Vector3f position;
    private final Matrix4f camera;
    private float initialHalfXSize;
    private float rotationX;
    private float rotationY;
    private boolean update;

    /**
     * @param size        Initial half of size on X dimension of orthogonal projection.
     * @param X
     * @param Y
     * @param Z
     * @param heightByWidthRatio Height/Width screen ratio
     * @param rotationX
     * @param rotationY
     * @param alpha
     */
    public IsometricCamera3D(
        float size,
        float X,
        float Y,
        float Z,
        float heightByWidthRatio,
        float rotationX,
        float rotationY,
        float alpha
    )
    {
        this.aboveYAlphaOverride = alpha;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.position = new Vector3f( X, Y, Z );
        this.heightByWidthRatio = heightByWidthRatio;
        this.initialHalfXSize = size;
        camera = new Matrix4f();
        update = false;
    }

    public void setRotationY( float rotationY ) {
        update = false;
        this.rotationY = rotationY;
    }

    public void setRotationX( float rotationX ) {
        update = false;
        this.rotationX = rotationX;
    }

    public void setSize( float size ) {
        update = false;
        this.initialHalfXSize = size;
    }

    public final void setCenterPoint( float X, float Y, float Z ) {
        update = false;
        this.position.set( X, Y, Z );
    }

    @Override
    public Matrix4fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }

    @Override
    public Vector3fc getCenterPoint() {
        return position;
    }

    @Override
    public float getAboveMaxYAlphaChannel() {
        return aboveYAlphaOverride;
    }

    @Override
    public boolean contains(
        float minX, float maxX, float minY, float maxY, float minZ, float maxZ
    )
    {
        return true;
    }

    private void updateMatrix() {
        if ( !update ) {
            float planeX = initialHalfXSize;
            //float planeZ = (size+1)*(size+1);
            float planeY = ( planeX ) * heightByWidthRatio;
            float planeZ = 5 + planeX;
            planeZ *= planeZ;
            //planeZ +=105;
            camera.setOrtho( -planeX, planeX, -planeY, planeY, -planeZ, planeZ );
            camera.rotateX( ( float ) java.lang.Math.toRadians( 30 + rotationX ) );
            camera.rotateY( ( float ) java.lang.Math.toRadians( 45.0 + rotationY ) );
            camera.translate( -position.x, 0, -position.z );
            update = false;
        }
    }

}
