/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.*;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RegistrableScene2D extends RenderableScene2D {
    
    void registerObject2D( PositionableObject2D< ?, ? > object );
    
    void deregisterObject2D( PositionableObject2D< ?, ? > object );
    
    void registerTerrain2D( Terrain2D< ?, ? > terrain, Index2D index );
    
    void deregisterTerrain2D( Terrain2D< ?, ? > terrain, Index2D index );
    
    void deregisterAllObjects2D();
    
    void setScene2DCamera( Camera2D camera );
    
    default void deregisterTerrain2D( Terrain2D< ?, ? > terrain, int x, int y ) {
        registerTerrain2D( terrain, Index2D.id2( x, y ) );
    }
    
    default void registerTerrain2D( Terrain2D< ?, ? > terrain, int x, int y ) {
        registerTerrain2D( terrain, Index2D.id2( x, y ) );
    }
    
    default void deregisterMultipleObjects2D( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::deregisterObject2D );
    }
    
    default void deregisterMultipleTerrains2D( Terrain2D< ?, ? > terrain, Collection< Index2D > indices ) {
        indices.forEach( index -> deregisterTerrain2D( terrain, index ) );
    }
    
    default void registerMultipleObjects2D( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::registerObject2D );
    }
    
    default void registerMultipleTerrains2D( Terrain2D< ?, ? > terrain, Collection< Index2D > indices ) {
        indices.forEach( index -> registerTerrain2D( terrain, index ) );
    }
    
    default void dispose() {
        deregisterAllObjects2D();
    }
    
    default void registerMultipleTerrains2D( Terrain2D< ?, ? >[][] terrainsXY ) {
        for ( var x = 0; x < terrainsXY.length; x++ ) {
            for ( var y = 0; y < terrainsXY[ x ].length; y++ ) {
                registerTerrain2D( terrainsXY[ x ][ y ], x, y );
            }
        }
    }
}
