/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RegistrableOverlay extends RenderableOverlay {
    
    void setOverlayCamera( Camera2D camera );
    
    void registerOverlay( PositionableObject2D< ?, ? > object );
    
    void deregisterOverlay( PositionableObject2D< ?, ? > object );
    
    void deregisterAllOverlays();
    
    default void registerMultipleOverlays( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::registerOverlay );
    }
    
    default void deregisterMultipleOverlays( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::deregisterOverlay );
    }
    
    default void dispose() {
        deregisterAllOverlays();
    }
}
