/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

import com.firststory.firstoracle.WindowSettings;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class IsometricCamera3D extends Camera3D {
    
    private final float aboveYAlphaOverride;
    private final Vector3f position;
    private final Matrix4f camera;
    private final WindowSettings settings;
    private float initialHalfXSize;
    private float rotationX;
    private float rotationY;
    
    /**
     * @param settings  window settings
     * @param size      Initial half of size on X dimension of orthogonal projection.
     * @param X         x position
     * @param Y         y position
     * @param Z         z position
     * @param rotationX rotation around x axis
     * @param rotationY rotation around y axis
     * @param alpha     max alpha channel
     */
    public IsometricCamera3D(
        WindowSettings settings,
        float size,
        int X,
        int Y,
        int Z,
        int rotationX,
        int rotationY,
        int alpha
    ) {
        this.settings = settings;
        this.aboveYAlphaOverride = alpha;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.position = new Vector3f( X, Y, Z );
        this.initialHalfXSize = size;
        camera = new Matrix4f();
    }
    
    public void setRotationY( float rotationY ) {
        if ( this.rotationY != rotationY ) {
            this.rotationY = rotationY;
            forceUpdate();
        }
    }
    
    public void setRotationX( float rotationX ) {
        if ( this.rotationX != rotationX ) {
            this.rotationX = rotationX;
            forceUpdate();
        }
    }
    
    public void setSize( float size ) {
        if ( this.initialHalfXSize != size ) {
            this.initialHalfXSize = size;
            forceUpdate();
        }
    }
    
    public final void setCenterPoint( float X, float Y, float Z ) {
        if ( position.x != X || position.y != Y || position.z != Z ) {
            this.position.set( X, Y, Z );
            forceUpdate();
        }
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
    public Matrix4fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }
    
    @Override
    public boolean contains(
        float minX, float maxX, float minY, float maxY, float minZ, float maxZ
    ) {
        return true;
    }
    
    private void updateMatrix() {
        if ( mustUpdate() ) {
            var planeX = initialHalfXSize;
            //float planeZ = (size+1)*(size+1);
            var planeY = ( planeX ) * settings.getHeightByWidthRatio();
            var planeZ = 5 + planeX;
            planeZ *= planeZ;
            //planeZ +=105;
            camera.setOrtho( -planeX, planeX, -planeY, planeY, -planeZ, planeZ );
            camera.rotateX( ( float ) java.lang.Math.toRadians( 30 + rotationX ) );
            camera.rotateY( ( float ) java.lang.Math.toRadians( 45.0 + rotationY ) );
            camera.translate( -position.x, 0, -position.z );
            updated();
        }
    }
    
    @Override
    public float getGeneralRotation() {
        return rotationY;
    }
}
