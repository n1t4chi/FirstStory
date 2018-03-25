/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera3D;

/**
 * @author n1t4chi
 */
public class CameraBoxZDimension {
    private final float minZ;
    private final float maxZ;
    
    public CameraBoxZDimension( float minZ, float maxZ ) {
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    public float getMaxZ() {
        return maxZ;
    }
    
    public float getMinZ() {
        return minZ;
    }
    
}
