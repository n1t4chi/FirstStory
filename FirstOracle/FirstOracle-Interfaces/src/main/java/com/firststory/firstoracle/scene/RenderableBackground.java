/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public interface RenderableBackground {
    
    Camera2D getBackgroundCamera();
    
    Colour getBackgroundColour();
    
    Collection< PositionableObject2D< ?, ? > > getBackgroundObjects();
    
    default List< RenderData > getBackgroundRenderData(
        double currentRenderTime,
        double cameraRotation
    ) {
        return getBackgroundObjects()
            .stream()
            .flatMap( object -> object.getRenderData( currentRenderTime, cameraRotation ).stream() )
            .collect( Collectors.toList() )
        ;
    }
}