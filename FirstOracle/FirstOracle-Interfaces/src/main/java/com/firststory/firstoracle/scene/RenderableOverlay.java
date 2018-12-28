/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public interface RenderableOverlay {
    
    Camera2D getOverlayCamera();
    
    Collection< PositionableObject2D< ?, ? > > getOverlayObjects();
    
    default List< RenderData > getOverlayRenderData( double currentRenderTime, double cameraRotation ) {
        return getOverlayObjects()
            .stream()
            .flatMap( object -> object.getRenderData( currentRenderTime, cameraRotation ).stream() )
            .collect( Collectors.toList() )
        ;
    }
}