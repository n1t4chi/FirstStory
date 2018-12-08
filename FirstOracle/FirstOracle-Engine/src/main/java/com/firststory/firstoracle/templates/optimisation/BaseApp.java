/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.controller.*;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.scene.RegistrableScene;
import com.firststory.firstoracle.templates.FpsCounter;
import com.firststory.firstoracle.window.*;

import java.util.List;
import java.util.function.*;

import static com.firststory.firstoracle.FirstOracleConstants.arraySize;

/**
 * @author n1t4chi
 */
public class BaseApp {
    
    private static final int DEFAULT_SCENE = 0;
    static final BiConsumer< SceneData, RegistrableWindow > createNonOptimisedScene = ( data, window ) -> window.createNewScene(
        DEFAULT_SCENE,
        data.terrain2dSize,
        data.terrain2dShift,
        data.terrain3dSize,
        data.terrain3dShift
    );
    static final BiConsumer< SceneData, RegistrableWindow > createOptimisedScene = ( data, window ) -> {
        window.createNewOptimisedScene(
            DEFAULT_SCENE,
            data.terrain2dSize,
            data.terrain2dShift,
            data.terrain3dSize,
            data.terrain3dShift
        );
    };
    private Consumer< RegistrableWindow > createScene;
    
    BaseApp( RegistrableScene scene ) {
        createScene = ( window ) -> window.registerScene( DEFAULT_SCENE, scene );
    }
    
    BaseApp(
        Terrain2D< ?, ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D,
        BiConsumer< SceneData, RegistrableWindow > createScene
    ) {
        this(
            terrains2D,
            terrain2dShift,
            renderables2D,
            new Terrain3D[ 0 ][][],
            FirstOracleConstants.INDEX_ZERO_3I,
            List.of(),
            createScene
        );
    }
    
    BaseApp(
        Terrain3D< ?, ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D,
        BiConsumer< SceneData, RegistrableWindow > createScene
    ) {
        this(
            new Terrain2D[ 0 ][],
            FirstOracleConstants.INDEX_ZERO_2I,
            List.of(),
            terrains3D,
            terrain3dShift,
            renderables3D,
            createScene
        );
    }
    
    private BaseApp(
        Terrain2D< ?, ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D,
        Terrain3D< ?, ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D,
        BiConsumer< SceneData, RegistrableWindow > createScene
    ) {
        var data = new SceneData(
            terrains2D,
            terrain2dShift,
            arraySize( terrains2D ),
            terrains3D,
            terrain3dShift,
            arraySize( terrains3D ),
            renderables3D,
            renderables2D
        );
        this.createScene = window -> {
            createScene.accept( data, window );
    
            window.deregisterMultipleObjects2D(
                DEFAULT_SCENE,
                data.renderables2D
            );
            window.registerMultipleObjects3D(
                DEFAULT_SCENE,
                renderables3D
            );
    
            window.registerMultipleTerrains3D(
                DEFAULT_SCENE,
                terrains3D
            );
            window.registerMultipleTerrains2D(
                DEFAULT_SCENE,
                terrains2D
            );
        };
    }
    
    public void run() {
        var width = 300;
        var height = 300;
        var settings = WindowSettings.builder()
            .setVerticalSync( false )
            .setResizeable( true )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build()
        ;
        var window = WindowBuilder.registrableWindow( settings ).build();
        var cameraController = CameraController.createAndStart(
            window,
            settings,
            CameraKeyMap.getFunctionalKeyLayout(),
            10,
            15f
        );
    
        createScene.accept( window );
    
    
        window.setBackgroundColour(
            DEFAULT_SCENE,
            FirstOracleConstants.WHITE
        );
        window.setScene2DCamera(
            DEFAULT_SCENE,
            cameraController.getCamera2D()
        );
        window.setScene3DCamera(
            DEFAULT_SCENE,
            cameraController.getCamera3D()
        );
        
        var fpsCounter = new FpsCounter( window );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                fpsCounter.setTextPosition(
                    0,
                    0
                );
            }
        } );
        window.registerOverlay(
            DEFAULT_SCENE,
            fpsCounter
        );
        
        window.run();
    }
    
    private class SceneData {
        
        private final Terrain2D< ?, ? >[][] terrains2D;
        private final Index2D terrain2dShift;
        private final Index2D terrain2dSize;
        private final Terrain3D< ?, ? >[][][] terrains3D;
        private final Index3D terrain3dShift;
        private final Index3D terrain3dSize;
        private final List< PositionableObject3D< ?, ? > > renderables3D;
        private final List< PositionableObject2D< ?, ? > > renderables2D;
        
        private SceneData(
            Terrain2D< ?, ? >[][] terrains2D,
            Index2D terrain2dShift,
            Index2D terrain2dSize,
            Terrain3D< ?, ? >[][][] terrains3D,
            Index3D terrain3dShift,
            Index3D terrain3dSize,
            List< PositionableObject3D< ?, ? > > renderables3D,
            List< PositionableObject2D< ?, ? > > renderables2D
        ) {
            this.terrains2D = terrains2D;
            this.terrain2dShift = terrain2dShift;
            this.terrain2dSize = terrain2dSize;
            this.terrains3D = terrains3D;
            this.terrain3dShift = terrain3dShift;
            this.terrain3dSize = terrain3dSize;
            this.renderables3D = renderables3D;
            this.renderables2D = renderables2D;
        }
    }
}
