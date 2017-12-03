/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Terrain2D;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface Terrain2DRenderer extends GraphicObjectRenderer< Terrain2D > {
    
    default void renderAll(
        Terrain2D[][] terrains, Vector2ic terrainShift, Vector4fc terrainOverlayColour, float maxAlphaChannel
    )
    {
        for ( int x = 0; x < terrains.length; x++ ) {
            for ( int y = 0; y < terrains[x].length; y++ ) {
                Terrain2D terrain = terrains[x][y];
                render( terrain, terrain.computePosition( x, y, terrainShift ), terrainOverlayColour, maxAlphaChannel );
            }
        }
    }
    
    default void renderAll( Terrain2D[][] terrains ) {
        renderAll( terrains, FirstOracleConstants.VECTOR_ZERO_2I, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    @Override
    default void render( Terrain2D terrain ) {
        render( terrain, FirstOracleConstants.VECTOR_ZERO_2F, FirstOracleConstants.VECTOR_ZERO_4F, 1f );
    }
    
    void render(
        Terrain2D terrain, Vector2fc terrainPosition, Vector4fc terrainOverlayColour, float maxAlphaChannel
    );
    
}
