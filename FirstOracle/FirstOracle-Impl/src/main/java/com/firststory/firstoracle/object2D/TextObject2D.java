/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.text.*;
import com.firststory.firstoracle.window.Window;

import java.awt.geom.Rectangle2D;

/**
 * @author n1t4chi
 */
public class TextObject2D
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Plane2DVertices >
    implements
        ResolutionBasedObject2D
{
    private final WindowSettings settings;
    private final TextImageFactory factory;
    private TextData textData;
    private final Rectangle2D rectangle2D = new Rectangle2D.Double();
    private int posX;
    private int posY;
    
    public TextObject2D( Window window, String text ) {
        this( window, TextImageFactory.provide(), text );
    }
    
    public TextObject2D( Window window, TextImageFactory factory, String text ) {
        this( window, factory );
        setText( text );
    }
    
    public TextObject2D( Window window ) {
        this( window, TextImageFactory.provide() );
    }
    
    public TextObject2D( Window window, TextImageFactory factory ) {
        this( window.getSettings(), factory );
        window.addWindowListener( this );
    }
    
    public TextObject2D( WindowSettings settings, TextImageFactory factory ) {
        this.settings = settings;
        this.factory = factory;
        textData = FirstOracleConstants.EMPTY_TEXT;
        setTransformations( new MutablePositionable2DTransformations() );
        setOverlayColour( FirstOracleConstants.BLACK );
    }
    
    public void setText( String text ) {
        this.textData = factory.createText3D( text );
        this.setTexture( textData.getTexture() );
        updateBounds();
    }
    
    public void setTextCenterPosition( int x, int y ) {
        var bounds = textData.getBounds();
        setTextPosition(
            ( int ) ( x - bounds.getWidth() / 2 ),
            ( int ) ( y - bounds.getHeight() / 2 )
        );
    }
    
    public void setTextPosition( int x, int y ) {
        this.posX = x;
        this.posY = y;
        updateBounds();
    }
    
    @Override
    public void updateBounds() {
        var bounds = textData.getBounds();
        rectangle2D.setFrame( bounds.getX() + posX, bounds.getY() + posY, bounds.getWidth(), bounds.getHeight() );
        ResolutionBasedObject2D.super.updateBounds();
    }
    
    @Override
    public Rectangle2D getBounds() {
        return rectangle2D;
    }
    
    @Override
    public WindowSettings getSettings() {
        return settings;
    }
}
