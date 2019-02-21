/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.StaticRectangle;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.window.Window;

import java.io.IOException;

/**
 * @author n1t4chi
 */
class MainMenu implements ControllableScene {
    
    private final RegistrableOverlayImpl overlay = new RegistrableOverlayImpl();
    private final RenderableBackgroundImpl renderableBackground = new RenderableBackgroundImpl( FirstOracleConstants.BLACK );
    private final SceneController sceneController;
    private StaticRectangle title;
    private Button exit;
    private Button newGame;
    private int screenWidth;
    private int screenHeight;
    private double startTime;
    private float currentTransparency;
    
    MainMenu( SceneController sceneController ) {
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
    public void click( double x, double y ) {
        var posX = x / screenWidth * 2 - 1;
        var posY = - y / screenHeight * 2 + 1;
        if( exit.isInside( posX, posY ) ) {
            sceneController.exit();
        } else if( newGame.isInside( posX, posY ) ) {
            sceneController.newGame();
        }
    }
    
    @Override
    public void currentTime( double newTimeSnapshot ) {
        if( currentTransparency >= 1 ) {
            return;
        }
        if( startTime < 0 ) {
            startTime = newTimeSnapshot;
        }
        var x = newTimeSnapshot - startTime;
        if( x > 2 )
        {
            sceneController.endTechScene();
            title.setMaxAlphaChannel( 1f );
            exit.setMaxAlphaChannel( 1f );
            newGame.setMaxAlphaChannel( 1f );
            currentTransparency = 1f;
        } else {
            currentTransparency = calcTransparency( x );
            title.setMaxAlphaChannel( currentTransparency );
            exit.setMaxAlphaChannel( currentTransparency );
            newGame.setMaxAlphaChannel( currentTransparency );
        }
    }
    
    @Override
    public void windowSize( int width, int height ) {
        updateSize( width, height );
    }
    
    @Override
    public void initialise( Window window ) {
        initTitleButton( window );
        initNewGameButton( window );
        initExitButton( window );
        title.setMaxAlphaChannel( 0f );
        exit.setMaxAlphaChannel( 0f );
        newGame.setMaxAlphaChannel( 0f );
        updateSize( window.getSettings().getWidth(), window.getSettings().getHeight() );
    }
    
    MainMenu reset() {
        startTime = -1;
        currentTransparency = 0;
        return this;
    }
    
    private void updateSize( int screenWidth, int screenHeight ) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        var halfWidth = screenWidth / 2;
        var quarterHeight = screenHeight / 4;
        exit.setCenterPosition( halfWidth, quarterHeight - 20 );
        newGame.setCenterPosition( halfWidth, quarterHeight + 20 );
    
        var screenRatio = (float) screenWidth / screenHeight;
        var imageRatio = title.getTexture().getRatio();
    
        var width = 0.5f / screenRatio * imageRatio;
        var height = 0.5f;
        if( width > 0.9 ) {
            height = height / ( width / 0.9f );
            width = 0.9f;
        }
        title.setScale( Scale2D.scale2( width, height ) );
    }
    
    private void initTitleButton( Window window ) {
        title = new StaticRectangle();
        try {
            var texture = Texture.create( "resources/First Slave/FirstStory.png" );
            title.setTexture( texture );
            title.setPosition( Position2D.pos2( 0, 0.25f ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Cannot load title" );
        }
        overlay.registerOverlay( title );
        
    }
    
    private void initExitButton( Window window ) {
        exit = new Button( window, "Exit" );
        exit.register( overlay );
    }
    
    private void initNewGameButton( Window window ) {
        newGame = new Button( window, "New Game" );
        newGame.register( overlay );
    }
    
    private float calcTransparency( double x ) {
        return ( float ) ( ( ( -0.266667 * x + 0.8 ) * x - 0.0333333 ) * x );
    }
}
