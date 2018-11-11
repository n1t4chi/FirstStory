/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.notyfying.WindowListener;
import com.firststory.firstoracle.notyfying.WindowSizeEvent;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.templates.FpsCounter;
import com.firststory.firstoracle.window.RegistrableWindow;
import com.firststory.firstoracle.window.WindowBuilder;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author n1t4chi
 */
public class BaseApp {
    private static final int DEFAULT_SCENE = 0;
    static final BiConsumer< BaseApp, RegistrableWindow > createNonOptimisedScene = ( thisApp, window ) ->
        window.createNewScene(
            DEFAULT_SCENE,
            thisApp.terrain2dSize,
            thisApp.terrain2dShift,
            thisApp.terrain3dSize,
            thisApp.terrain3dShift
        );
    static final BiConsumer< BaseApp, RegistrableWindow > createOptimisedScene = ( thisApp, window ) ->
        window.createNewOptimisedScene(
            DEFAULT_SCENE,
            thisApp.terrain2dSize,
            thisApp.terrain2dShift,
            thisApp.terrain3dSize,
            thisApp.terrain3dShift
        );
    
    private final Terrain2D<?>[][] terrains2D;
    private final Index2D terrain2dShift;
    private final Index2D terrain2dSize;
    private final Terrain3D<?>[][][] terrains3D;
    private final Index3D terrain3dShift;
    private final Index3D terrain3dSize;
    private final List< PositionableObject3D< ?, ? > > renderables3D;
    private final List< PositionableObject2D< ?, ? > > renderables2D;
    private final BiConsumer< BaseApp, RegistrableWindow > createScene;
    
    BaseApp(
        Terrain2D< ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D,
        BiConsumer< BaseApp, RegistrableWindow > createScene
    ) {
        this(
            terrains2D,
            terrain2dShift,
            renderables2D,
            new Terrain3D[0][][],
            FirstOracleConstants.INDEX_ZERO_3I,
            List.of(),
            createScene
        );
    }
    
    BaseApp(
        Terrain3D< ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D,
        BiConsumer< BaseApp, RegistrableWindow > createScene
    ) {
        this(
            new Terrain2D[0][],
            FirstOracleConstants.INDEX_ZERO_2I,
            List.of(),
            terrains3D,
            terrain3dShift,
            renderables3D,
            createScene
        );
    }
    
    private BaseApp(
        Terrain2D< ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D,
        Terrain3D< ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D,
        BiConsumer< BaseApp, RegistrableWindow > createScene
    ) {
        this.terrains2D = terrains2D;
        this.terrain2dShift = terrain2dShift;
        this.terrains3D = terrains3D;
        this.terrain3dShift = terrain3dShift;
        this.renderables3D = renderables3D;
        this.renderables2D = renderables2D;
        terrain2dSize = Index2D.id2(
            terrains2D.length,
            terrains2D.length == 0 ? 0 : terrains2D[0].length
        );
        terrain3dSize = Index3D.id3(
            terrains3D.length,
            terrains3D.length == 0 ? 0 : terrains3D[0].length,
            terrains3D.length == 0 || terrains3D[0].length == 0 ? 0 : terrains3D[0][0].length
        );
        this.createScene = createScene;
    }
    
    public void run() {
        
        var width = 300;
        var height = 300;
        var settings = WindowSettings
            .builder()
            .setVerticalSync( false )
            .setResizeable( true )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build();
        var window = WindowBuilder.registrableWindow( settings ).build();
        var cameraController = CameraController.createAndStart(
            window,
            settings,
            CameraKeyMap.getFunctionalKeyLayout(),
            10,
            15f
        );
        
        createScene.accept( this, window );
    
        window.deregisterMultipleObjects2D( DEFAULT_SCENE, renderables2D );
        window.registerMultipleObjects3D( DEFAULT_SCENE, renderables3D );
    
        window.registerMultipleTerrains3D( DEFAULT_SCENE, terrains3D );
        window.registerMultipleTerrains2D( DEFAULT_SCENE, terrains2D );
    
        window.setBackgroundColour( DEFAULT_SCENE, FirstOracleConstants.WHITE );
        window.setScene2DCamera( DEFAULT_SCENE, cameraController.getCamera2D() );
        window.setScene3DCamera( DEFAULT_SCENE, cameraController.getCamera3D() );
    
        var fpsCounter = new FpsCounter( window );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                fpsCounter.setTextPosition( 0, 0 );
            }
        } );
        window.registerOverlay( DEFAULT_SCENE, fpsCounter );
    
        window.run();
    }
}
