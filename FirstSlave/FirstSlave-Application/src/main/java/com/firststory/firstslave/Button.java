/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.scene.RegistrableOverlayImpl;
import com.firststory.firstoracle.text.TextImageFactory;
import com.firststory.firstoracle.window.Window;

/**
 * @author n1t4chi
 */
class Button {
    private final TextObject2D label;
    private final PositionableObject2DImpl background;
    
    Button( Window window, String start ) {
        label = new TextObject2D( window, TextImageFactory.provide( 30 ), start );
        background = new PositionableObject2DImpl();
        background.setVertices( label.getVertices() );
        background.setTransformations( label.getTransformations() );
        background.setOverlayColour( FirstOracleConstants.WHITE );
    }
    
    void setMaxAlphaChannel( float v ) {
        label.setMaxAlphaChannel( v );
        background.setMaxAlphaChannel( v );
    }
    
    void setCenterPosition( int x, int y ) {
        label.setTextCenterPosition( x, y );
    }
    
    void register( RegistrableOverlayImpl overlay ) {
        overlay.registerOverlay( background );
        overlay.registerOverlay( label );
    }
    
    boolean isInside( double x, double y ) {
        return label.getBBO().contains( x, y );
    }
}
