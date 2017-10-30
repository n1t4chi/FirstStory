/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle.object;

import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class BoundingBox {
    
    private final float minX, maxX, minY, maxY, minZ, maxZ;
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public float getMaxZ() {
        return maxZ;
    }
    
    public float getMinX() {
        return minX;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public float getMinZ() {
        return minZ;
    }
    
    public BoundingBox( float minX, float maxX, float minY, float maxY, float minZ, float maxZ ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    public BoundingBox getTransformedBoundingBox( ObjectTransformations transformations ) {
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = this.minX;
        maxX = this.maxX;
        minY = this.minY;
        maxY = this.maxY;
        minZ = this.minZ;
        maxZ = this.maxZ;
        Vector3fc rotation = transformations.getRotation();
        Vector3fc position = transformations.getPosition();
        Vector3fc scale = transformations.getScale();
        if ( rotation.lengthSquared() != 0 ) {
            minX *= 1.5;
            maxX *= 1.5;
            minY *= 1.5;
            maxY *= 1.5;
            minZ *= 1.5;
            maxZ *= 1.5;
        }
        minX *= scale.x();
        minX += position.x();
        maxX *= scale.x();
        maxX += position.y();
        minY *= scale.y();
        minY += position.y();
        maxY *= scale.y();
        maxY += position.y();
        minZ *= scale.z();
        minZ += position.z();
        maxZ *= scale.z();
        maxZ += position.z();
        return new BoundingBox( minX, maxX, minY, maxY, minZ, maxZ );
    }
}
