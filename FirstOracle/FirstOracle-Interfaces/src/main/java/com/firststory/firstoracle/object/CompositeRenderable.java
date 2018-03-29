/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

import java.util.Collection;

public interface CompositeRenderable extends Renderable {
    
    Collection< Renderable > getObjectsToRender();
    
    @Override
    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getObjectsToRender().forEach( object -> object.render( renderingContext, currentRenderTime, cameraDataProvider ) );
    }
}
