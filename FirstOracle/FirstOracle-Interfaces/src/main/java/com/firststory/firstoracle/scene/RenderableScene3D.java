/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderableScene3D {
    
    Camera3D getScene3DCamera();
    
    Collection< PositionableObject3D< ?, ? > > getObjects3D();
    
    Terrain3D< ? >[][][] getTerrains3D();
    
    Index3D getTerrain3DShift();
    
    default void renderScene3D(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render3D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation3D();
            
            var terrainShift = getTerrain3DShift();
            var terrainsXYZ = getTerrains3D();
            for ( int x = 0, xLength = terrainsXYZ.length; x < xLength; x++ ) {
                var terrainsYZ = terrainsXYZ[ x ];
                
                for ( int y = 0, yLength = terrainsYZ.length; y < yLength; y++ ) {
                    var terrainsZ = terrainsYZ[ y ];
                    
                    for ( int z = 0, zLength = terrainsZ.length; z < zLength; z++ ) {
                        var terrain = terrainsZ[ z ];
                        
                        renderer.renderObject( terrain, terrain.computePosition( x, y, z, terrainShift ), currentRenderTime, cameraRotation );
                    }
                }
            }
            
            for ( var object : getObjects3D() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
}
