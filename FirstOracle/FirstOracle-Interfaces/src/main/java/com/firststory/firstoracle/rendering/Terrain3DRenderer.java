/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.Terrain3D;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Terrain3DRenderer< Terrain extends Terrain3D > extends Object3DRenderer< Terrain > {
    
    default void renderAll(
        Terrain[][][] terrains, Vector3ic terrainShift, Vector4fc terrainOverlayColour, float maxAlphaChannel
    )
    {
        for ( int x = 0; x < terrains.length; x++ ) {
            for ( int y = 0; y < terrains[x].length; y++ ) {
                for ( int z = 0; z < terrains[x][y].length; z++ ) {
                    Terrain terrain = terrains[x][y][z];
                    renderTerrain(
                        terrain,
                        terrain.computePosition( x, y, z, terrainShift ),
                        terrainOverlayColour,
                        maxAlphaChannel
                    );
                }
            }
        }
    }
    
    default void renderAll( Terrain[][][] terrains ) {
        renderAll( terrains, FirstOracleConstants.VECTOR_ZERO_3I, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    @Override
    default void render( Terrain object ) {
        renderTerrain( object, FirstOracleConstants.VECTOR_ZERO_3F, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    void renderTerrain(
        Terrain terrain, Vector3fc terrainPosition, Vector4fc terrainOverlayColour, float maxAlphaChannel
    );
}