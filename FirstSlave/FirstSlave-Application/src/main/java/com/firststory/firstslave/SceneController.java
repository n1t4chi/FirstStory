/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.window.*;
import com.firststory.firsttools.PropertyUtils;

import java.util.*;

/**
 * @author n1t4chi
 */
class SceneController implements SceneProvider, MouseListener, KeyListener, WindowListener, TimeListener, QuitListener {
    
    private double x = 0;
    private double y = 0;
    
    private final TechScene techScene = new TechScene( this );
    private final MainMenu mainMenu = new MainMenu( this );
    private final GameLoading gameLoading = new GameLoading( this );
    private final GameScene gameScene = new GameScene( this );
    private final GameMenu gameMenu = new GameMenu( this, gameScene );
    private final List< ControllableScene > scenes = List.of(
        techScene,
        mainMenu,
        gameLoading,
        gameScene,
        gameMenu
    );
    private ControllableScene currentScene = PropertyUtils.isPropertyTrue( "SkipDemo" ) ? mainMenu : techScene;
    private Window window;
    
    @Override
    public RenderableScene getNextScene() {
        return currentScene;
    }
    
    @Override
    public Collection< Thread > notify( QuitEvent event, QuitNotifier source ) {
        exit();
        return Collections.emptyList();
    }
    
    void endTechScene() {
        currentScene = mainMenu.reset();
    }
    
    void continueGame() {
        currentScene = gameScene.continueGame();
    }
    
    void newGame() {
        currentScene = gameLoading.reset();
        new Thread( () -> {
            try {
                currentScene = gameScene.prepareNewGame();
            } catch ( Exception ex ) {
                ex.printStackTrace();
                exit();
            }
        } ).start();
    }
    
    void quitGame() {
        currentScene = mainMenu.reset();
    }
    
    void gameMenu() {
        currentScene = gameMenu;
    }
    
    void exit() {
        window.quit();
        scenes.forEach( RenderableScene::dispose );
    }
    
    void initialise( Window window ) {
        this.window = window;
        scenes.forEach( scene -> scene.initialise( window ) );
        window.addMouseListener( this );
        window.addKeyListener( this );
        window.addWindowListener( this );
        window.addTimeListener( this );
        window.addQuitListener( this );
    }
    
    @Override
    public void notify( MousePositionEvent event ) {
        x = event.xpos;
        y = event.ypos;
    }
    
    @Override
    public void notify( KeyEvent event ) {
        event.getAction().doIfPress( () -> currentScene.keyPress( event.getKey() ) );
        event.getAction().doIfRelease( () -> currentScene.keyRelease( event.getKey() ) );
    }
    
    @Override
    public void notify( MouseButtonEvent event ) {
        event.getAction().doIfPress( () ->  currentScene.click( x, y ) );
    }
    
    @Override
    public void notify( MouseScrollEvent event ) {
        currentScene.scroll( event.yoffset );
    }
    
    @Override
    public void notify( WindowSizeEvent event ) {
        scenes.forEach( scene -> scene.windowSize( event.getWidth(), event.getHeight() ) );
    }
    
    @Override
    public void notify( double newTimeSnapshot, TimeNotifier source ) {
        currentScene.currentTime( newTimeSnapshot );
    }
}
