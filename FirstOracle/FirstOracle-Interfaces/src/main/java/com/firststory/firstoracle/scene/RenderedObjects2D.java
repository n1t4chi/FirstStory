/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object.CompositeRenderable;
import com.firststory.firstoracle.object.Renderable;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector2ic;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderedObjects2D extends CompositeRenderable {
    
    Terrain2D[][] getTerrains();
    
    Collection< Renderable > getObjects();
    
    Vector2ic getTerrainShift();
    
    @Override
    default Collection< Renderable > getObjectsToRender() {
        return getObjects();
    }
    
    @Override
    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    )
    {
        Terrain2D[][] terrains = getTerrains();
        for ( int x = 0; x < terrains.length; x++ ) {
            for ( int y = 0; y < terrains[x].length; y++ ) {
                Terrain2D terrain = terrains[x][y];
                terrain.bindPosition( renderingContext, x, y, getTerrainShift() );
                terrain.render( renderingContext, currentRenderTime, cameraDataProvider );
            }
        }
        getObjectsToRender().forEach( object -> object.render( renderingContext, currentRenderTime, cameraDataProvider ) );
    }
}