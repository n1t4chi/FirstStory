/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RenderableBackground {
    
    Camera2D getBackgroundCamera();
    
    Colour getBackgroundColour();
    
    Collection< PositionableObject2D< ?, ? > > getBackgroundObjects();
    
    default void renderBackground(
        RenderingContext renderingContext, double currentRenderTime, CameraDataProvider cameraDataProvider
    ) {
        renderingContext.render2D( renderer -> {
            var cameraRotation = cameraDataProvider.getCameraRotation2D();
            for ( var object : getBackgroundObjects() ) {
                renderer.render( object, currentRenderTime, cameraRotation );
            }
        } );
    }
}