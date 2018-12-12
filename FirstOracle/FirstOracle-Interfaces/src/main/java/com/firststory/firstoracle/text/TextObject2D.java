/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.text;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.window.Window;

/**
 * @author n1t4chi
 */
public class TextObject2D
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >
    implements
        StaticObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >,
        PositionableObject2D< MutablePositionable2DTransformations, AbsolutePlane2DVertices >,
        WindowListener
{
    private final WindowSettings settings;
    private final TextImageFactory factory;
    private TextData textData;
    private int posX;
    private int posY;
    private Colour colour = FirstOracleConstants.BLACK;
    
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
    }
    
    @Override
    public Texture getTexture() {
        return textData.getTexture();
    }
    
    @Override
    public UvMap getUvMap() {
        return PlaneUvMap.getPlaneUvMap();
    }
    
    @Override
    public AbsolutePlane2DVertices getVertices() {
        return AbsolutePlane2DVertices.getPlane2DVertices();
    }
    
    @Override
    public int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
    
    @Override
    public Colour getOverlayColour() {
        return colour;
    }
    
    public void setColour( Colour colour ) {
        this.colour = colour;
    }
    
    public void setText( String text ) {
        this.textData = factory.createText3D( text );
        update();
    }
    
    public void setTextPosition( int x, int y ) {
        posX = x;
        posY = y;
    }
    
    public int getPosX() {
        return posX;
    }
    
    public int getPosY() {
        return posY;
    }
    
    @Override
    public void notify( WindowSizeEvent event ) {
        update();
    }
    
    @Override
    public void update() {
        getTransformations().setScale( textData.computeScale2D( settings.getWidth(), settings.getHeight() ) );
        getTransformations().setPosition( textData.computePosition2D( posX, posY, settings.getWidth(), settings.getHeight() ) );
        super.update();
    }
}
