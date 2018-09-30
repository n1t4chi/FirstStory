/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author n1t4chi
 */
public class RegistrableOverlayImpl implements RegistrableOverlay {
    
    private final Set< PositionableObject2D< ?, ? > > objects = new HashSet<>();
    private Camera2D camera = IdentityCamera2D.getCamera();
    
    @Override
    public void setOverlayCamera( Camera2D camera ) {
        this.camera = camera;
    }
    
    @Override
    public void registerOverlay( PositionableObject2D< ?, ? > object ) {
        objects.add( object );
    }
    
    @Override
    public void deregisterOverlay( PositionableObject2D< ?, ? > object ) {
        objects.remove( object );
    }
    
    @Override
    public void deregisterAllOverlays() {
        objects.clear();
    }
    
    @Override
    public Camera2D getOverlayCamera() {
        return camera;
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getOverlayObjects() {
        return objects;
    }
}
