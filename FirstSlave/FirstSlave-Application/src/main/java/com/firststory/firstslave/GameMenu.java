/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.object2D.StaticRectangle;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.window.Window;

/**
 * @author n1t4chi
 */
class GameMenu implements ControllableScene {
    
    private final RegistrableOverlayImpl overlay = new RegistrableOverlayImpl();
    private final RenderableBackgroundImpl renderableBackground = new RenderableBackgroundImpl( FirstOracleConstants.WHITE );
    private final SceneController sceneController;
    private final RenderableScene3D scene3D;
    private Button continueGame;
    private Button newGame;
    private Button mainMenu;
    private Button exit;
    private StaticRectangle staticRectangle;
    private int screenWidth;
    private int screenHeight;
    
    GameMenu( SceneController sceneController, GameScene gameScene ) {
        
        this.sceneController = sceneController;
        scene3D = gameScene.getScene3D();
    }
    
    @Override
    public RenderableScene2D getScene2D() {
        return EmptyRenderableScene2D.provide();
    }
    
    @Override
    public RenderableScene3D getScene3D() {
        return scene3D;
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
        } else if( mainMenu.isInside( posX, posY ) ) {
            sceneController.quitGame();
        } else if( newGame.isInside( posX, posY ) ) {
            sceneController.newGame();
        } else if( continueGame.isInside( posX, posY ) ) {
            sceneController.continueGame();
        }
    }
    
    @Override
    public void windowSize( int width, int height ) {
        updateSize( width, height );
    }
    
    @Override
    public void initialise( Window window ) {
        initGreyOut( window );
        initContinueButton( window );
        initNewGameButton( window );
        initMainMenuButton( window );
        initExitButton( window );
        updateSize( window.getSettings().getWidth(), window.getSettings().getHeight() );
    }
    
    private void initGreyOut( Window window ) {
        staticRectangle = new StaticRectangle();
        staticRectangle.setOverlayColour( Colour.col( 0.7f, 0.7f, 0.7f, 0.3f ) );
        overlay.registerOverlay( staticRectangle );
    }
    
    private void updateSize( int screenWidth, int screenHeight ) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        continueGame.setCenterPosition( screenWidth/2, screenHeight/2 + 90 );
        newGame.setCenterPosition( screenWidth/2, screenHeight/2 + 30 );
        mainMenu.setCenterPosition( screenWidth/2, screenHeight/2 - 30 );
        exit.setCenterPosition( screenWidth/2, screenHeight/2 - 90 );
    }
    
    private void initContinueButton( Window window ) {
        continueGame = new Button( window, "Continue" );
        continueGame.register( overlay );
    }
    
    private void initNewGameButton( Window window ) {
        newGame = new Button( window, "New Game" );
        newGame.register( overlay );
    }
    
    private void initMainMenuButton( Window window ) {
        mainMenu = new Button( window, "Main Menu" );
        mainMenu.register( overlay );
    }
    
    private void initExitButton( Window window ) {
        exit = new Button( window, "Exit" );
        exit.register( overlay );
    }
}
