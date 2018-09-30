/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.object.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RegistrableScene3D extends RenderableScene3D {
    
    void registerObject3D( PositionableObject3D< ?, ? > object );
    
    void deregisterObject3D( PositionableObject3D< ?, ? > object );
    
    void registerTerrain3D( Terrain3D< ? > terrain, Index3D index );
    
    void deregisterTerrain3D( Terrain3D< ? > terrain, Index3D index );
    
    void deregisterAllObjects3D();
    
    void setScene3DCamera( Camera3D camera );
    
    default void registerTerrain3D( Terrain3D< ? > terrain, int x, int y, int z ) {
        registerTerrain3D( terrain, Index3D.id3( x, y, z ) );
    }
    
    default void deregisterTerrain3D( Terrain3D< ? > terrain, int x, int y, int z ) {
        deregisterTerrain3D( terrain, Index3D.id3( x, y, z ) );
    }
    
    default void deregisterMultipleObjects3D( Collection< PositionableObject3D< ?, ? > > objects ) {
        objects.forEach( this::deregisterObject3D );
    }
    
    default void deregisterMultipleTerrains3D( Terrain3D< ? > terrain, Collection< Index3D > positions ) {
        positions.forEach( position -> deregisterTerrain3D( terrain, position ) );
    }
    
    default void registerMultipleObjects3D( Collection< PositionableObject3D< ?, ? > > objects ) {
        objects.forEach( this::registerObject3D );
    }
    
    default void registerMultipleTerrains3D( Terrain3D< ? > terrain, Collection< Index3D > indices ) {
        indices.forEach( index -> registerTerrain3D( terrain, index ) );
    }
    
    default void dispose() {
        deregisterAllObjects3D();
    }
    
    default void registerMultipleTerrains3D( Terrain3D<?>[][][] terrainsXYZ ) {
        for ( var x = 0; x < terrainsXYZ.length; x++ ) {
            for ( var y = 0; y < terrainsXYZ[ x ].length; y++ ) {
                for ( var z = 0; z < terrainsXYZ[ x ][ y ].length; z++ ) {
                    registerTerrain3D( terrainsXYZ[ x ][ y ][ x ], x, y, z );
                }
            }
        }
    }
    
}
