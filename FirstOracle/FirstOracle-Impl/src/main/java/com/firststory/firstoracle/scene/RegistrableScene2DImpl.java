/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author n1t4chi
 */
public class RegistrableScene2DImpl implements RegistrableScene2D {
    
    private final Set< PositionableObject2D< ?, ? > > objects = new HashSet<>();
    private final Terrain2D< ? >[][] terrainsXY;
    private final Index2D terrainShift;
    private Camera2D camera = IdentityCamera2D.getCamera();
    
    public RegistrableScene2DImpl( int terrainXSize, int terrainYSize, Index2D terrainShift ) {
        this( new Terrain2D[ terrainXSize ][ terrainYSize ], terrainShift );
    }
    
    public RegistrableScene2DImpl( Terrain2D< ? >[][] terrains, Index2D terrainShift ) {
        this.terrainShift = terrainShift;
        terrainsXY = terrains;
    }
    
    @Override
    public void registerObject2D( PositionableObject2D< ?, ? > object ) {
        objects.add( object );
    }
    
    @Override
    public void deregisterObject2D( PositionableObject2D< ?, ? > object ) {
        objects.remove( object );
    }
    
    @Override
    public void registerTerrain2D( Terrain2D< ? > terrain, Index2D index ) {
        terrainsXY[index.x()][index.y()] = terrain;
    }
    
    @Override
    public void deregisterTerrain2D( Terrain2D< ? > terrain, Index2D index ) {
        terrainsXY[index.x()][index.y()] = null;
    }
    
    @Override
    public void deregisterAllObjects2D() {
        objects.clear();
        for ( var terrainsY : terrainsXY ) {
            Arrays.fill( terrainsY, null );
        }
    }
    
    @Override
    public void setScene2DCamera( Camera2D camera ) {
        this.camera = camera;
    }
    
    @Override
    public Camera2D getScene2DCamera() {
        return camera;
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getObjects2D() {
        return objects;
    }
    
    @Override
    public Terrain2D< ? >[][] getTerrains2D() {
        return terrainsXY;
    }
    
    @Override
    public Index2D getTerrain2DShift() {
        return terrainShift;
    }
}
