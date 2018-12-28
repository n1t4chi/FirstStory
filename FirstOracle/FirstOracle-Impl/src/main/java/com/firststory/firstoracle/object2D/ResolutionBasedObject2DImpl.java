/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.window.Window;

import java.awt.geom.Rectangle2D;

/**
 * @author n1t4chi
 */
public class ResolutionBasedObject2DImpl
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >
    implements
        ResolutionBasedObject2D
{
    private final WindowSettings settings;
    private Texture texture = FirstOracleConstants.EMPTY_TEXTURE;
    private Rectangle2D bounds = new Rectangle2D.Double();
    private Colour colour = FirstOracleConstants.WHITE;
    
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
    
    @Override
    public WindowSettings getSettings() {
        return settings;
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture( Texture texture )
    {
        this.texture = texture;
    }
    
    @Override
    public Colour getOverlayColour() {
        return colour;
    }
    
    public void setColour( Colour colour ) {
        this.colour = colour;
    }
}
