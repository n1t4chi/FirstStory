/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.object.Renderable;
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
    }
    
    Renderable getScene2D();
    
    default void renderScene3D(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getScene3D().render( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    Renderable getScene3D();
    
    default void renderBackground(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getBackground().render( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    Renderable getBackground();
    
    default void renderOverlay(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getOverlay().render( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    Renderable getOverlay();
    
    Camera3D getCamera3D();
    
    Camera2D getCamera2D();
    
    Vector4fc getBackgroundColour();
}
