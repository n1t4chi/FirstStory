/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;


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
