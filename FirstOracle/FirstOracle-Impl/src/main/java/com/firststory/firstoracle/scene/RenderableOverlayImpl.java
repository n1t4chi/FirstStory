/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;
import java.util.List;

/**
 * @author n1t4chi
 */
public class RenderableOverlayImpl implements RenderableOverlay {
    
    private final Camera2D camera;
    private final List< PositionableObject2D< ?, ? > > objects;
    
    public RenderableOverlayImpl( Camera2D camera, List< PositionableObject2D< ?, ? > > objects ) {
        this.camera = camera;
        this.objects = objects;
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
