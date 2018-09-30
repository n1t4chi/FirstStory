/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface RegistrableBackground extends RenderableBackground {
    
    void registerBackground( PositionableObject2D< ?, ? > object );
    
    void deregisterBackground( PositionableObject2D< ?, ? > object );
    
    void deregisterAllBackgrounds();
    
    void setBackgroundCamera( Camera2D camera );
    
    void setBackgroundColour( Colour colour );
    
    default void deregisterMultipleBackgrounds( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::deregisterBackground );
    }
    
    default void registerMultipleBackgrounds( Collection< PositionableObject2D< ?, ? > > objects ) {
        objects.forEach( this::registerBackground );
    }
    
    default void dispose() {
        deregisterAllBackgrounds();
    }
}
