/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector3ic;

/**
 * @author n1t4chi
 */
public interface RenderedScene3D extends RenderedObjects3D {
    
    Terrain3D[][][] getTerrains();
    Vector3ic getTerrainShift();
    
    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        getObjects().forEach( object -> { object.render( renderingContext, currentRenderTime, cameraDataProvider ); } );
    }
}
