/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.*;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object2D.PositionableObject2D;

import java.util.*;

/**
 * @author n1t4chi
 */
public class RenderableBackgroundImpl implements RenderableBackground {
    
    private final List< PositionableObject2D< ?, ? > > objects;
    private final Camera2D camera;
    private final Colour backgroundColour;
    
    public RenderableBackgroundImpl( Colour backgroundColour ) {
        this(
            IdentityCamera2D.getCamera(),
            Collections.emptyList(),
            backgroundColour
        );
    }
    
    public RenderableBackgroundImpl( Camera2D camera, List< PositionableObject2D< ?, ? > > objects, Colour backgroundColour ) {
        this.objects = objects;
        this.camera = camera;
        this.backgroundColour = backgroundColour;
    }
    
    @Override
    public Camera2D getBackgroundCamera() {
        return camera;
    }
    
    @Override
    public Collection< PositionableObject2D< ?, ? > > getBackgroundObjects() {
        return objects;
    }
    
    @Override
    public Colour getBackgroundColour() {
        return backgroundColour;
    }
}
