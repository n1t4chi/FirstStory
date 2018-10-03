/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderableScene2D {
    
    Camera2D getScene2DCamera();
    
    Collection< PositionableObject2D< ?, ? > > getObjects2D();
    
    Terrain2D< ? >[][] getTerrains2D();
    
    Index2D getTerrain2DShift();
    
    default void renderScene2D(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            
            var terrainShift = getTerrain2DShift();
            var terrains = getTerrains2D();
            for ( int x = 0, xLength = terrains.length; x < xLength; x++ ) {
                var terrainsY = terrains[ x ];
                
                for ( int y = 0, yLength = terrainsY.length; y < yLength; y++ ) {
                    var terrain = terrainsY[ y ];
    
                    renderer.render( terrain.getRenderData( terrain.computePosition( x, y, terrainShift ), currentRenderTime, cameraRotation ) );
                    
                }
            }
            
            for ( var object : getObjects2D() ) {
                renderer.render( object.getRenderData( currentRenderTime, cameraRotation ) );
            }
        } );
    }
}
