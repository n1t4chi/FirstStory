/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.text.TextImageFactory;
import com.firststory.firstoracle.window.Window;

import java.awt.geom.Rectangle2D;

/**
 * @author n1t4chi
 */
public class GameLoading implements ControllableScene {
    private final RegistrableOverlayImpl overlay = new RegistrableOverlayImpl();
    private final RenderableBackgroundImpl renderableBackground = new RenderableBackgroundImpl( FirstOracleConstants.BLACK );
    private final SceneController sceneController;
    private TextObject2D[] loadingLabels = new TextObject2D[4];
    private double startTime;
    private int labelWidth = Integer.MIN_VALUE;
    
    GameLoading( SceneController sceneController ) {
        this.sceneController = sceneController;
        reset();
    }
    
    @Override
    public RenderableScene2D getScene2D() {
        return EmptyRenderableScene2D.provide();
    }
    
    @Override
    public RenderableScene3D getScene3D() {
        return EmptyRenderableScene3D.provide();
    }
    
    @Override
    public RenderableBackground getBackground() {
        return renderableBackground;
    }
    
    @Override
    public RenderableOverlay getOverlay() {
        return overlay;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void windowSize( int width, int height ) {
        updateSize( width, height );
    }
    
    @Override
    public void initialise( Window window ) {
        initLoadingLabels( window );
        updateSize( window.getSettings().getWidth(), window.getSettings().getHeight() );
    }
    
    @Override
    public void currentTime( double newTimeSnapshot ) {
        if( startTime < 0 ) {
            startTime = newTimeSnapshot;
        }
        var time = newTimeSnapshot - startTime;
        var index = ( ( int ) Math.floor( time ) ) % loadingLabels.length;
        for ( var i = 0 ; i < loadingLabels.length; i ++ ) {
            loadingLabels[i].setMaxAlphaChannel( i == index ? 1f : 0f );
        }
    }
    
    ControllableScene reset() {
        startTime = -1;
        return this;
    }
    
    private void updateSize( int screenWidth, int screenHeight ) {
        for ( var loadingLabel : loadingLabels ) {
            loadingLabel.setTextPosition(
                screenWidth - labelWidth - 30,
                30
            );
        }
    }
    
    private void initLoadingLabels( Window window ) {
        var dots = new StringBuilder();
        for( var i =0 ; i < loadingLabels.length ; i ++ ) {
            loadingLabels[i] = createLoadingLabel( window, dots.toString() );
            Rectangle2D bounds = loadingLabels[ i ].getBounds();
            labelWidth = Math.max( labelWidth, (int) bounds.getWidth() );
            dots.append( "." );
        }
        loadingLabels[0].setMaxAlphaChannel( 1f );
    }
    
    private TextObject2D createLoadingLabel( Window window, String dots ) {
        var loadingLabel = new TextObject2D( window,
            TextImageFactory.provide( 20 ),
            "Loading" + dots
        );
        loadingLabel.setOverlayColour( FirstOracleConstants.WHITE );
        overlay.registerOverlay( loadingLabel );
        loadingLabel.setMaxAlphaChannel( 0f );
        return loadingLabel;
    }
}
