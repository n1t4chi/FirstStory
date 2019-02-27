/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.text.TextImageFactory;
import com.firststory.firstoracle.window.Window;

import java.io.IOException;

import static com.firststory.firstoracle.data.Colour.col;

/**
 * @author n1t4chi
 */
public class TechScene implements ControllableScene {
    private final RegistrableOverlayImpl overlay = new RegistrableOverlayImpl();
    private final RenderableBackgroundImpl renderableBackground = new RenderableBackgroundImpl( FirstOracleConstants.BLACK );
    private final SceneController sceneController;
    private TextObject2D poweredByLabel;
    private ResolutionBasedObject2DImpl firstOracleLogo;
    private final int logoWidth = 300;
    private float logoHeight;
    private double startTime = -1;
    private float currentTransparency = 0;
    
    TechScene( SceneController sceneController ) {
        this.sceneController = sceneController;
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
    public void currentTime( double newTimeSnapshot ) {
        if( startTime < 0 ) {
            startTime = newTimeSnapshot;
        }
        var x = newTimeSnapshot - startTime;
        if( x > 6 ) {
            sceneController.endTechScene();
            firstOracleLogo.setMaxAlphaChannel( 0f );
            poweredByLabel.setMaxAlphaChannel( 0f );
            currentTransparency = 0;
        } else {
            currentTransparency = calcTransparency( x );
            firstOracleLogo.setMaxAlphaChannel( currentTransparency );
            poweredByLabel.setMaxAlphaChannel( currentTransparency );
        }
    }
    
    @Override
    public void windowSize( int width, int height ) {
        updateSize( width, height );
    }
    
    @Override
    public void initialise( Window window ) {
        initFirstOracleLogo( window );
        initPoweredByLabel( window );
        firstOracleLogo.setMaxAlphaChannel( 0f );
        poweredByLabel.setMaxAlphaChannel( 0f );
        updateSize( window.getSettings().getWidth(), window.getSettings().getHeight() );
    }
    
    private void updateSize( int screenWidth, int screenHeight ) {
        var width = screenWidth - logoWidth - 20;
        var height = logoHeight + 20;
        poweredByLabel.setTextPosition(
            ( int ) (width - poweredByLabel.getBounds().getWidth() ),
            55
        );
        firstOracleLogo.setPosition( width, height );
    }
    
    private void initFirstOracleLogo( Window window ) {
        firstOracleLogo = new ResolutionBasedObject2DImpl( window );
        try {
            firstOracleLogo.setTexture( Texture.create( "resources/First Oracle/FirstOracle.png" ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Cannot load title" );
        }
        logoHeight = logoWidth / firstOracleLogo.getTexture().getRatio();
        firstOracleLogo.setSize( logoWidth, logoHeight );
        overlay.registerOverlay( firstOracleLogo );
    }
    
    private void initPoweredByLabel( Window window ) {
        poweredByLabel = new TextObject2D( window, TextImageFactory.provide( 20 ), "Powered by:" );
        poweredByLabel.setOverlayColour( col( 0, 0.5f, 0, 1 ) );
        overlay.registerOverlay( poweredByLabel );
    }
    
    private float calcTransparency( double x ) {
        if( x > 3 ) {
            x = 6 - x;
        }
        return ( float ) ( ( ( -0.0790123 * x + 0.355556 ) * x - 0.0222222 ) * x );
    }
}
