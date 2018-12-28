/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.*;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.*;

/**
 * @author n1t4chi
 */
public class RegistrableBackgroundImpl implements RegistrableBackground {
    
    private final Set< PositionableObject2D< ?, ? > > objects = new LinkedHashSet<>();
    private Camera2D camera = IdentityCamera2D.getCamera();
    private Colour colour = FirstOracleConstants.BLACK;
    
    @Override
    public void registerBackground( PositionableObject2D< ?, ? > object ) {
        objects.add( object );
    }
    
    @Override
    public void deregisterBackground( PositionableObject2D< ?, ? > object ) {
        objects.remove( object );
    }
    
    @Override
    public void deregisterAllBackgrounds() {
        objects.clear();
    }
    
    @Override
    public Camera2D getBackgroundCamera() {
        return camera;
    }
    
    @Override
    public void setBackgroundCamera( Camera2D camera ) {
        this.camera = camera;
    }
    
    @Override
    public Colour getBackgroundColour() {
        return colour;
    }
    
    @Override
    public void setBackgroundColour( Colour colour ) {
        this.colour = colour;
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getBackgroundObjects() {
        return objects;
    }
}
