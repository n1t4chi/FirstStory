/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle.camera;

import com.firststory.firstoracle.Camera;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class IsometricCamera extends Camera {
    
    private final float widthToHeightRatio;
    private float initialHalfXSize;
    
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
    
    private final float aboveYAlphaOverride;
    private float rotationX;
    private float rotationY;
    private final Vector3f position;
    private final Matrix4f camera;
    private boolean update;
    
    /**
     * @param size        Initial half of size on X dimension of orthogonal projection.
     * @param X
     * @param Y
     * @param Z
     * @param screenRatio Height/Width screen ratio
     * @param rotationX
     * @param rotationY
     * @param alpha
     */
    public IsometricCamera(
                              float size,
                              float X,
                              float Y,
                              float Z,
                              float screenRatio,
                              float rotationX,
                              float rotationY,
                              float alpha
    )
    {
        this.aboveYAlphaOverride = alpha;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.position = new Vector3f( X, Y, Z );
        this.widthToHeightRatio = screenRatio;
        this.initialHalfXSize = size;
        camera = new Matrix4f();
        update = false;
    }
    
    private void updateMatrix() {
        if ( !update ) {
            float planeX = initialHalfXSize;
            //float planeZ = (size+1)*(size+1);
            float planeY = ( planeX ) * widthToHeightRatio;
            float planeZ = 5 + planeX;
            planeZ *= planeZ;
            //planeZ +=105;
            camera.setOrtho( -planeX, planeX, -planeY, planeY, -planeZ, planeZ );
            camera.rotateX( ( float ) java.lang.Math.toRadians( 30 + rotationX ) );
            camera.rotateY( ( float ) java.lang.Math.toRadians( 45.0 + rotationY ) );
            camera.translate( -position.x, 0, -position.y );
            update = false;
        }
    }
    
    @Override
    public Matrix4fc getMatrixRepresentation() {
        updateMatrix();
        return camera;
    }
    
    @Override
    public boolean contains(
                               float minX,
                               float maxX,
                               float minY,
                               float maxY,
                               float minZ,
                               float maxZ
    )
    {
        return false;
    }
    
    @Override
    public Vector3fc getCenterPoint() {
        return position;
    }
    
    @Override
    public float getAboveMaxYAlphaChannel() {
        return aboveYAlphaOverride;
    }
    
}
