/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Terrain;

import static com.firststory.objects3D.HexPrismUtil.getUVMapHex;
import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import java.io.IOException;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 *
 * @author n1t4chi
 */
public abstract class Terrain3D extends Object3D{
    private static final Vector3fc ROTATION = new Vector3f(0,0,0);
    private static final Vector3fc SCALE = new Vector3f(1,1,1);
    
    @Override
    public Vector3fc getRotation() {
        return ROTATION;
    }
    @Override
    public Vector3fc getScale() {
        return SCALE;
    }

    
    /**
     * Initialises this object with given texture.
     * @param texture 
     */
    public Terrain3D(Texture texture) {
        super(texture);
    }

    public abstract Vector3fc convertArrayToSpacePosition(int x, int y,int z,Vector3ic terrainMin);
    public abstract Vector3ic convertSpaceToArrayPosition(float x, float y, float z);
    /**
     * Returns whether the given terrain would be visible or not.
     * @param x X dimension coordinate of object
     * @param y Y dimension coordinate of object
     * @param z Z dimension coordinate of object
     * @param terrainMin array dimensions shift
     * @param terrain array with terrain data
     * @param terrainSize size of array
     * @return 
     */
    public boolean isVisible(int x, int y, int z,Vector3ic terrainMin,Terrain3D[][][] terrain,Vector3ic terrainSize){
        return isVisibleArrayPos(x+terrainMin.x(),y+terrainMin.y(),z+terrainMin.z(),terrain,terrainSize);
    }
    /**
     * Returns whether the given terrain would be visible or not.
     * @param x first dimension index in array
     * @param y second dimension index in array
     * @param z third dimension index in array 
     * @param terrain array with terrain data
     * @param terrainSize size of array
     * @return 
     */
    public abstract boolean isVisibleArrayPos(int x,int y,int z,Terrain3D[][][] terrain,Vector3ic terrainSize);
    
}
