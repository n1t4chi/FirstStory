/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import com.firststory.firstoracle.object.BoundingBox;
import com.firststory.firstoracle.object.Object3D;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

/**
 *
 * @author n1t4chi
 */
public abstract class Camera {

    public abstract Vector3fc getCenterPoint();
    
    public abstract float getAboveMaxYAlphaChannel();
    
    public abstract Matrix4fc getMatrixRepresentation();
    
    public abstract boolean contains(
        float minX,
        float maxX,
        float minY,
        float maxY,
        float minZ,
        float maxZ
    );
    public boolean contains(float X, float Y, float Z){
        return contains(X,X,Y,Y,Z,Z);
    }
    
    public boolean contains( Object3D object3D ) {
        BoundingBox bb = object3D.getBBO();
        return contains(
            bb.getMinX(),
            bb.getMaxX(),
            bb.getMinY(),
            bb.getMaxY(),
            bb.getMinZ(),
            bb.getMaxZ()
        );
    }
    
    
    
    
}
