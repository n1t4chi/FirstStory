/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

/**
 * @author n1t4chi
 */
public interface RenderableScene {
    
    RenderableScene2D getScene2D();
    
    RenderableScene3D getScene3D();
    
    RenderableBackground getBackground();
    
    RenderableOverlay getOverlay();
    
    void dispose();
    
    default void renderScene2D( RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider ) {
        getScene2D().renderScene2D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    default void renderScene3D( RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider ) {
        getScene3D().renderScene3D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    default void renderBackground( RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider ) {
        getBackground().renderBackground( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    default void renderOverlay( RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider ) {
        getOverlay().renderOverlay( renderingContext, currentRenderTime, cameraDataProvider );
    }
}
