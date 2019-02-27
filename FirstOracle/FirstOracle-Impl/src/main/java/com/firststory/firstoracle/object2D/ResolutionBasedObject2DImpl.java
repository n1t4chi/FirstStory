/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.window.Window;

import java.awt.geom.Rectangle2D;

/**
 * @author n1t4chi
 */
public class ResolutionBasedObject2DImpl
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Plane2DVertices >
    implements
        ResolutionBasedObject2D
{
    private final WindowSettings settings;
    private Rectangle2D bounds = new Rectangle2D.Double();
    
    public ResolutionBasedObject2DImpl( Window window ) {
        this.settings = window.getSettings();
        setTransformations( new MutablePositionable2DTransformations() );
        window.addWindowListener( this );
    }
    
    public void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
    }
    
    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }
    
    public void setPosition( double posX, double posY ) {
        bounds.setRect( posX, posY, bounds.getWidth(), bounds.getHeight() );
        updateBounds();
    }
    
    public void setSize( double width, double height ) {
        bounds.setRect( bounds.getX(), bounds.getY(), width, height );
        updateBounds();
    }
    
    @Override
    public WindowSettings getSettings() {
        return settings;
    }
}
