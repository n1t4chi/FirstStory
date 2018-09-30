/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderableOverlay {
    
    Camera2D getOverlayCamera();
    
    Collection< PositionableObject2D< ?, ? > > getOverlayObjects();
    
    default void renderOverlay(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            for ( var object : getOverlayObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
}