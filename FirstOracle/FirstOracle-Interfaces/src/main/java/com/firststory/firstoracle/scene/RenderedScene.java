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
        getScene2D().render( renderingContext, currentRenderTime, cameraDataProvider );
    
//        renderingContext.render2D( renderer -> {
//            double cameraRotation = cameraDataProvider.getCameraRotation2D();
//            RenderedScene2D scene2D = getScene2D();
//
//            Vector2ic terrainShift = scene2D.getTerrainShift();
//            Terrain2D<?>[][] terrains = scene2D.getTerrains();
//            for ( int x = 0, xLength = terrains.length; x < xLength; x++ ) {
//                Terrain2D< ? >[] terrainsY = terrains[ x ];
//
//                for ( int y = 0, yLength = terrainsY.length; y < xLength; y++ ) {
//                    Terrain2D<?> terrain = terrainsY[ y ];
//
//                    renderer.render(
//                        terrain,
//                        terrain.computePosition( x, y, terrainShift ),
//                        currentRenderTime,
//                        cameraRotation
//                    );
//
//                }
//            }
//
//            for ( PositionableObject2D< ?, ? > object : scene2D.getObjects() ) {
//                renderer.render( object, currentRenderTime, cameraRotation );
//            }
//        } );
    }
    
    RenderedScene2D getScene2D();
    
    default void renderScene3D(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getScene3D().render( renderingContext, currentRenderTime, cameraDataProvider );
        
//        renderingContext.render3D( renderer -> {
//            double cameraRotation = cameraDataProvider.getCameraRotation3D();
//            RenderedScene3D scene3D = getScene3D();
//
//            Vector3ic terrainShift = scene3D.getTerrainShift();
//            Terrain3D<?>[][][] terrainsXYZ = scene3D.getTerrains();
//            for ( int x = 0, xLength = terrainsXYZ.length; x < xLength; x++ ) {
//                Terrain3D< ? >[][] terrainsYZ = terrainsXYZ[ x ];
//
//                for ( int y = 0, yLength = terrainsYZ.length; y < yLength; y++ ) {
//                    Terrain3D< ? >[] terrainsZ = terrainsYZ[ y ];
//
//                    for ( int z = 0, zLength = terrainsZ.length; z < zLength; z++ ) {
//                        Terrain3D<?> terrain = terrainsZ[ z ];
//
//                        renderer.render(
//                            terrain,
//                            terrain.computePosition( x, y, z, terrainShift ),
//                            currentRenderTime,
//                            cameraRotation
//                        );
//                    }
//                }
//            }
//
//            for ( PositionableObject3D< ?, ? > object : scene3D.getObjects() ) {
//                renderer.render( object, currentRenderTime, cameraRotation );
//            }
//        } );
    }
    
    RenderedScene3D getScene3D();
    
    default void renderBackground(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getBackground().render( renderingContext, currentRenderTime, cameraDataProvider );
    
//        renderingContext.render2D( renderer -> {
//            double cameraRotation = cameraDataProvider.getCameraRotation2D();
//            RenderedObjects2D scene2D = getBackground();
//
//            for ( PositionableObject2D< ?, ? > object : scene2D.getObjects() ) {
//                renderer.render( object, currentRenderTime, cameraRotation );
//            }
//        } );
    }
    
    RenderedObjects2D getBackground();
    
    default void renderOverlay(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getOverlay().render( renderingContext, currentRenderTime, cameraDataProvider );
        
//        renderingContext.render2D( renderer -> {
//            double cameraRotation = cameraDataProvider.getCameraRotation2D();
//            RenderedObjects2D scene2D = getOverlay();
//
//            for ( PositionableObject2D< ?, ? > object : scene2D.getObjects() ) {
//                renderer.render( object, currentRenderTime, cameraRotation );
//            }
//        } );
    }
    
    RenderedObjects2D getOverlay();
    
    Camera3D getCamera3D();
    
    Camera2D getCamera2D();
    
    Vector4fc getBackgroundColour();
}
