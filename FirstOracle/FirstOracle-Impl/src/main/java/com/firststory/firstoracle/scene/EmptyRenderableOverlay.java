/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
public class EmptyRenderableOverlay implements RenderableOverlay {
    
    public static final EmptyRenderableOverlay instance = new EmptyRenderableOverlay();
    
    public static EmptyRenderableOverlay provide() {
        return instance;
    }
    
    private EmptyRenderableOverlay() {}
    
    @Override
    public Camera2D getOverlayCamera() {
        return IdentityCamera2D.getCamera();
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getOverlayObjects() {
        return Collections.emptyList();
    }
}
