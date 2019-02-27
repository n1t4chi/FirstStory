/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.object3D.FullyAnimatedPlane3D;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.templates.IOUtilities;
import com.firststory.firstoracle.window.Window;

import java.util.concurrent.*;

/**
 * @author n1t4chi
 */
class GameScene implements ControllableScene {
    
    private final RegistrableOverlayImpl overlay = new RegistrableOverlayImpl();
    private final RenderableBackgroundImpl renderableBackground = new RenderableBackgroundImpl( FirstOracleConstants.WHITE );
    private final RegistrableScene3D scene3D = new OptimisedRegistrableScene3DImpl();
    
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool( 1 );
    private final SceneController sceneController;
    private Window window;
    private IsometricCamera3D camera;
    private float cameraSize = 20;
    
    private Hero hero;
    
    GameScene( SceneController sceneController ) {
        this.sceneController = sceneController;
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
    public void windowSize( int width, int height ) {
        if( camera != null ) {
            camera.forceUpdate();
        }
    }
    
    @Override
    public void dispose() {
        if( hero != null ) {
            hero.dispose();
        }
        executor.shutdown();
    }
    
    @Override
    public void scroll( double dx ) {
        cameraSize = ( cameraSize - ( float ) dx );
        updateCamera();
    }
    
    @Override
    public void keyPress( Key key ) {
        switch ( key.getKeyCode() ) {
            case KEY_ESCAPE -> pauseMenu();
            case KEY_UP, KEY_W -> hero.walkUp();
            case KEY_DOWN, KEY_S -> hero.walkDown();
            case KEY_LEFT, KEY_A -> hero.walkLeft();
            case KEY_RIGHT, KEY_D -> hero.walkRight();
        }
    }
    
    @Override
    public void keyRelease( Key key ) {
        switch ( key.getKeyCode() ) {
            case KEY_UP, KEY_W -> hero.stopWalkUp();
            case KEY_DOWN, KEY_S -> hero.stopWalkDown();
            case KEY_LEFT, KEY_A -> hero.stopWalkLeft();
            case KEY_RIGHT, KEY_D -> hero.stopWalkRight();
        }
    }
    
    @Override
    public void initialise( Window window ) {
        this.window = window;
    }
    
    GameScene prepareNewGame() {
        scene3D.deregisterAllObjects3D();
        loadScene3D();
        setUpCamera();
        return this;
    }
    
    void updateCamera() {
        camera.setSize( cameraSize );
        this.camera.setCenterPoint(
            hero.getPosition().x(),
            hero.getPosition().y(),
            hero.getPosition().z()
        );
    }
    
    GameScene continueGame() {
        hero.continueGame();
        return this;
    }
    
    private void setUpCamera() {
        this.camera = new IsometricCamera3D( window.getSettings() );
        updateCamera();
        scene3D.setScene3DCamera( camera );
    }
    
    private void pauseMenu() {
        hero.pauseGame();
        sceneController.gameMenu();
    }
    
    private void loadScene3D() {
        if( hero != null ) {
            hero.dispose();
        }
        try {
            var textResource = IOUtilities.readTextResource( "resources/First Slave/map1.json" );
            var sceneParser = new SceneParser( textResource );
            sceneParser.parseScene3D( textResource, scene3D );
            var heroObject = ( FullyAnimatedPlane3D ) sceneParser.createPositionableObject3DFromShared( "hero" );
            this.hero = new Hero( heroObject, executor, this );
            this.hero.register( scene3D );
        } catch ( Exception ex ) {
            throw new RuntimeException( "Cannot load scene", ex );
        }
    }
}
