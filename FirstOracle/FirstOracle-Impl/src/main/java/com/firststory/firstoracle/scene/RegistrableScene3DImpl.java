/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera3D.*;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.*;

import java.util.*;

import static com.firststory.firstoracle.FirstOracleConstants.arraySize;

/**
 * @author n1t4chi
 */
public class RegistrableScene3DImpl implements RegistrableScene3D {
    
    private final Set< PositionableObject3D< ?, ? > > objects = new LinkedHashSet<>();
    private final Terrain3D< ? >[][][] terrainsXYZ;
    private final Index3D terrainSize;
    private final Index3D terrainShift;
    private Camera3D camera = IdentityCamera3D.getCamera();
    
    public RegistrableScene3DImpl( Index3D terrainSize, Index3D terrainShift ) {
        this(
            terrainSize,
            new Terrain3D[ terrainSize.x() ][ terrainSize.y() ][ terrainSize.z() ],
            terrainShift
        );
    }
    
    public RegistrableScene3DImpl( Terrain3D< ? >[][][] terrains, Index3D terrainShift ) {
        this(
            arraySize( terrains ),
            terrains,
            terrainShift
        );
    }
    
    private RegistrableScene3DImpl(
        Index3D terrainSize,
        Terrain3D< ? >[][][] terrain,
        Index3D terrainShift
    ) {
        this.terrainSize = terrainSize;
        this.terrainShift = terrainShift;
        terrainsXYZ = terrain;
    }
    public Index3D getTerrainSize() {
        return terrainSize;
    }
    
    @Override
    public void registerObject3D( PositionableObject3D< ?, ? > object ) {
        objects.add( object );
    }
    
    @Override
    public void deregisterObject3D( PositionableObject3D< ?, ? > object ) {
        objects.remove( object );
    }
    
    @Override
    public void registerTerrain3D( Terrain3D< ? > terrain, Index3D index ) {
        terrainsXYZ[ index.x() ][ index.y() ][ index.z() ] = terrain;
    }
    
    @Override
    public void deregisterTerrain3D( Terrain3D< ? > terrain, Index3D index ) {
        terrainsXYZ[ index.x() ][ index.y() ][ index.z() ] = null;
    }
    
    @Override
    public void deregisterAllObjects3D() {
        objects.clear();
        for ( var terrainYZ : terrainsXYZ ) {
            for ( var terrainZ : terrainsXYZ ) {
                Arrays.fill( terrainZ, null );
            }
        }
    }
    
    @Override
    public Camera3D getScene3DCamera() {
        return camera;
    }
    
    @Override
    public void setScene3DCamera( Camera3D camera ) {
        this.camera = camera;
    }
    
    @Override
    public Collection< PositionableObject3D< ?, ? > > getObjects3D() {
        return objects;
    }
    
    @Override
    public Terrain3D< ? >[][][] getTerrains3D() {
        return terrainsXYZ;
    }
    
    @Override
    public Index3D getTerrain3DShift() {
        return terrainShift;
    }
}
