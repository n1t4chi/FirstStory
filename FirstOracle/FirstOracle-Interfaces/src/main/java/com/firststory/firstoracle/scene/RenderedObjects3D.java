/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object.CompositeRenderable;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector3ic;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderedObjects3D extends CompositeRenderable< PositionableObject3D > {
    
    Terrain3D[][][] getTerrains();
    
    Collection< PositionableObject3D > getObjects();
    
    Vector3ic getTerrainShift();
    
    @Override
    default Collection< PositionableObject3D > getObjectsToRender() {
        return getObjects();
    }
    
    @Override
    default void render(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    )
    {
        Terrain3D[][][] terrains = getTerrains();
        for ( int x = 0; x < terrains.length; x++ ) {
            for ( int y = 0; y < terrains[x].length; y++ ) {
                for ( int z = 0; z < terrains[x][y].length; z++ ) {
                    Terrain3D terrain = terrains[x][y][z];
                    terrain.bindPosition( renderingContext, x, y, z, getTerrainShift() );
                    terrain.render( renderingContext, currentRenderTime, cameraDataProvider );
                }
            }
        }
        getObjectsToRender().forEach( object -> {
            terrains[0][0][0].bindPosition( renderingContext, 6, 6, 6, getTerrainShift() );
            object.render( renderingContext, currentRenderTime, cameraDataProvider );
        } );
    }
}
