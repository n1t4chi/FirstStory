/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.*;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.*;

import java.util.*;

import static com.firststory.firstoracle.data.Index2D.*;

/**
 * @author n1t4chi
 */
public class RegistrableScene2DImpl implements RegistrableScene2D {
    
    private final Set< PositionableObject2D< ?, ? > > objects = new LinkedHashSet<>();
    private Terrain2D< ?, ? >[][] terrainsXY;
    private Index2D terrainSize;
    private Index2D terrainShift;
    private Camera2D camera = IdentityCamera2D.getCamera();
    
    public RegistrableScene2DImpl( Index2D terrainSize, Index2D terrainShift ) {
        this( terrainSize, new Terrain2D[ terrainSize.x() ][ terrainSize.y() ], terrainShift );
    }
    
    public RegistrableScene2DImpl( Terrain2D< ?, ? >[][] terrains, Index2D terrainShift ) {
        this(
            FirstOracleConstants.arraySize( terrains ),
            terrains,
            terrainShift
        );
    }
    
    public RegistrableScene2DImpl() {
        this(
            FirstOracleConstants.INDEX_ZERO_2I,
            FirstOracleConstants.INDEX_ZERO_2I
        );
    }
    
    private RegistrableScene2DImpl( Index2D terrainSize, Terrain2D< ?, ? >[][] terrains, Index2D terrainShift ) {
        this.terrainSize = terrainSize;
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
    public void registerTerrain2D( Terrain2D< ?, ? > terrain, Index2D index ) {
        setTerrainAt( terrain, index );
    }
    
    @Override
    public void deregisterTerrain2D( Terrain2D< ?, ? > terrain, Index2D index ) {
        setTerrainAt( null, index );
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
    public Terrain2D< ?, ? >[][] getTerrains2D() {
        return terrainsXY;
    }
    
    @Override
    public Index2D getTerrain2DShift() {
        return terrainShift;
    }
    
    private void setTerrainAt( Terrain2D< ?, ? > terrain, Index2D index ) {
        var tabId = index.subtract( terrainShift );
        
        if( tabId.belowOrEqual( FirstOracleConstants.INDEX_ZERO_2I ) || terrainSize.belowOrEqual( tabId ) ) {
            var newTerrainSize = maxId2( terrainSize, tabId.increment() );
            var newTerrainShift = minId2( terrainShift, index );
            var newTerrainsXY = new Terrain2D[ newTerrainSize.x() ][ newTerrainSize.y() ];
            
            var dx = terrainShift.x() - newTerrainShift.x();
            var dy = terrainShift.y() - newTerrainShift.y();
            for( var x = 0; x < terrainSize.x() ; x++ ) {
                System.arraycopy( terrainsXY[ x ], 0, newTerrainsXY[ x + dx ], dy, terrainSize.y() );
            }
            
            tabId = index.subtract( terrainShift );
            terrainsXY = newTerrainsXY;
            terrainShift = newTerrainShift;
            terrainSize = newTerrainSize;
        }
        
        terrainsXY[ tabId.x() ][ tabId.y() ] = terrain;
    }
}
