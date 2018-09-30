/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.Collection;
import java.util.Collections;

/**
 * @author n1t4chi
 */
class EmptyRenderedBackground implements RenderableBackground {
    
    public static final EmptyRenderedBackground instance = new EmptyRenderedBackground();
    
    public static EmptyRenderedBackground provide() {
        return instance;
    }
    
    private EmptyRenderedBackground() {}
    
    @Override
    public Camera2D getBackgroundCamera() {
        return IdentityCamera2D.getCamera();
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getBackgroundObjects() {
        return Collections.emptyList();
    }
    
    @Override
    public Colour getBackgroundColour() {
        return FirstOracleConstants.BLACK;
    }
}
