/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera3D.*;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.*;

import java.util.*;

import static com.firststory.firstoracle.FirstOracleConstants.arraySize;
import static com.firststory.firstoracle.data.Index3D.*;

/**
 * @author n1t4chi
 */
public class RegistrableScene3DImpl implements RegistrableScene3D {
    
    private final Set< PositionableObject3D< ?, ? > > objects = new LinkedHashSet<>();
    private Terrain3D< ?, ? >[][][] terrainsXYZ;
    private Index3D terrainSize;
    private Index3D terrainShift;
    private Camera3D camera = IdentityCamera3D.getCamera();
    
    public RegistrableScene3DImpl( Index3D terrainSize, Index3D terrainShift ) {
        this(
            terrainSize,
            new Terrain3D[ terrainSize.x() ][ terrainSize.y() ][ terrainSize.z() ],
            terrainShift
        );
    }
    
    public RegistrableScene3DImpl( Terrain3D< ?, ? >[][][] terrains, Index3D terrainShift ) {
        this(
            arraySize( terrains ),
            terrains,
            terrainShift
        );
    }
    
    public RegistrableScene3DImpl() {
        this(
            FirstOracleConstants.INDEX_ZERO_3I,
            FirstOracleConstants.INDEX_ZERO_3I
        );
    }
    
    private RegistrableScene3DImpl(
        Index3D terrainSize,
        Terrain3D< ?, ? >[][][] terrains,
        Index3D terrainShift
    ) {
        this.terrainSize = terrainSize;
        this.terrainShift = terrainShift;
        terrainsXYZ = terrains;
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
    public void registerTerrain3D( Terrain3D< ?, ? > terrain, Index3D index ) {
        setTerrainAt( terrain, index );
    }
    
    @Override
    public void deregisterTerrain3D( Terrain3D< ?, ? > terrain, Index3D index ) {
        setTerrainAt( null, index );
    }
    
    @Override
    public void deregisterAllObjects3D() {
        objects.clear();
        for ( var terrainYZ : terrainsXYZ ) {
            for ( var terrainZ : terrainYZ ) {
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
    public Terrain3D< ?, ? >[][][] getTerrains3D() {
        return terrainsXYZ;
    }
    
    @Override
    public Index3D getTerrain3DShift() {
        return terrainShift;
    }
    
    private void setTerrainAt( Terrain3D< ?, ? > terrain, Index3D index ) {
        var tabId = index.subtract( terrainShift );
        
        if( tabId.belowOrEqual( FirstOracleConstants.INDEX_ZERO_3I ) || terrainSize.belowOrEqual( tabId ) ) {
            var newTerrainSize = maxId3( terrainSize, tabId.increment() );
            var newTerrainShift = minId3( terrainShift, tabId );
            var newTerrainsXYZ = new Terrain3D[ newTerrainSize.x() ][ newTerrainSize.y() ][ newTerrainSize.z() ];
            
            var dx = terrainShift.x() - newTerrainShift.x();
            var dy = terrainShift.y() - newTerrainShift.y();
            var dz = terrainShift.z() - newTerrainShift.z();
            for( var x = 0; x < terrainSize.x() ; x++ ) {
                for( var y = 0; y < terrainSize.y() ; y++ ) {
                    System.arraycopy( terrainsXYZ[ x ][ y ], 0, newTerrainsXYZ[ x + dx ][ y + dy ], dz, terrainSize.z() );
                }
            }
            
            tabId = index.subtract( terrainShift );
            terrainsXYZ = newTerrainsXYZ;
            terrainShift = newTerrainShift;
            terrainSize = newTerrainSize;
        }
        
        terrainsXYZ[ tabId.x() ][ tabId.y() ][ tabId.z() ] = terrain;
    }
}
