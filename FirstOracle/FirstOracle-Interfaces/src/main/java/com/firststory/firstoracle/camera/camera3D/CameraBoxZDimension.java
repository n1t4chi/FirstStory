/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera.camera3D;


public class CameraBoxZDimension {
    private final float minZ;
    private final float maxZ;

    public float getMaxZ() {
        return maxZ;
    }

    public float getMinZ() {
        return minZ;
    }

    public CameraBoxZDimension(float minZ, float maxZ) {
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
}
