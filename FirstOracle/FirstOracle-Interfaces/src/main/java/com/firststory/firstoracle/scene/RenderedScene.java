/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface RenderedScene {
    
    default void renderScene2D(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            var scene2D = getScene2D();
    
            var terrainShift = scene2D.getTerrainShift();
            var terrains = scene2D.getTerrains();
            for ( int x = 0, xLength = terrains.length; x < xLength; x++ ) {
                var terrainsY = terrains[ x ];
                
                for ( int y = 0, yLength = terrainsY.length; y < xLength; y++ ) {
                    var terrain = terrainsY[ y ];
                    
                    renderer.render( terrain,
                        terrain.computePosition( x, y, terrainShift ),
                        currentRenderTime,
                        cameraRotation
                    );
                    
                }
            }
            
            for ( var object : scene2D.getObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
    
    RenderedScene2D getScene2D();
    
    default void renderScene3D(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render3D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation3D();
            var scene3D = getScene3D();
    
            var terrainShift = scene3D.getTerrainShift();
            var terrainsXYZ = scene3D.getTerrains();
            for ( int x = 0, xLength = terrainsXYZ.length; x < xLength; x++ ) {
                var terrainsYZ = terrainsXYZ[ x ];
                
                for ( int y = 0, yLength = terrainsYZ.length; y < yLength; y++ ) {
                    var terrainsZ = terrainsYZ[ y ];
                    
                    for ( int z = 0, zLength = terrainsZ.length; z < zLength; z++ ) {
                        var terrain = terrainsZ[ z ];
                        
                        renderer.render( terrain,
                            terrain.computePosition( x, y, z, terrainShift ),
                            currentRenderTime,
                            cameraRotation
                        );
                    }
                }
            }
            
            for ( var object : scene3D.getObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
    
    RenderedScene3D getScene3D();
    
    default void renderBackground(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            var scene2D = getBackground();
            
            for ( var object : scene2D.getObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
    
    RenderedObjects2D getBackground();
    
    default void renderOverlay(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            var scene2D = getOverlay();
            
            for ( var object : scene2D.getObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
    
    RenderedObjects2D getOverlay();
    
    Camera3D getCamera3D();
    
    Camera2D getCamera2D();
    
    Vector4fc getBackgroundColour();
}
