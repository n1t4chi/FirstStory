/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.notyfying.WindowListener;
import com.firststory.firstoracle.notyfying.WindowSizeEvent;
import com.firststory.firstoracle.object.DefaultDirectionController;
import com.firststory.firstoracle.object.LoopedFrameController;
import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.scene.RenderableBackgroundImpl;
import com.firststory.firstoracle.scene.RenderableScene2DImpl;
import com.firststory.firstoracle.scene.RenderableSceneMutable;
import com.firststory.firstoracle.scene.SceneProvider;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.WindowBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class GlfwApplication2D {
    
    public static void main( String[] args ) throws Exception {
        new GlfwApplication2D().run();
    }
    
    private Window window;
    private SceneProvider sceneProvider;
    private CameraController cameraController;
    private RenderableSceneMutable renderedScene;
    private WindowSettings settings;
    
    public void run() throws Exception {
        var width = 300;
        var height = 300;
        settings = new WindowSettings.WindowSettingsBuilder().setVerticalSync( false )
            .setResizeable( true )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build();
        
        renderedScene = new RenderableSceneMutable( settings );
        var texture1 = new Texture( "resources/First Oracle/texture2D.png" );
        var texture2 = new Texture( "resources/First Oracle/obj.png" );
        var compoundTexture = Texture.createCompoundTexture( "resources/First Oracle/compound/tex_#frame#_#direction#.png",
            4,
            6
        );
        var terrain = new NonAnimatedRectangleGrid();
        terrain.setTexture( texture1 );
    
        var object = new NonAnimatedRectangle();
        object.setTransformations( new Mutable2DTransformations() );
        object.setTexture( texture2 );
        
        var directionController = new DefaultDirectionController( compoundTexture.getDirections() );
        var frameController = new LoopedFrameController();
        var compound = new AnimatedRectangle();
        frameController.setCurrentState( compoundTexture.getFrames(), 0, 1 );
        
        compound.setDirectionController( directionController );
        compound.setFrameController( frameController );
        compound.setUvMap( new PlaneUvMap( compoundTexture ) );
        compound.setTexture( compoundTexture );
        compound.setTransformations( new Mutable2DTransformations() );
        compound.getTransformations().setPosition( -4, -4 );
        compound.getTransformations().setScale( 4, 4 );
    
        var array = new RectangleGrid[ 20 ][ 20 ];
        
        for ( var x = 0; x < array.length; x++ ) {
            for ( var y = 0; y < array[ x ].length; y++ ) {
                array[ x ][ y ] = terrain;
            }
        }
        List< PositionableObject2D< ?, ? > > renderables = Arrays.asList( compound/*, object*/ );
    
        var camera2D = new MovableCamera2D( settings, 10, 0, 0, 0 );
        
        renderedScene.setBackground( new RenderableBackgroundImpl( IdentityCamera2D.getCamera(), Collections.emptyList(), FirstOracleConstants.WHITE ) );
        renderedScene.setScene2D( new RenderableScene2DImpl( camera2D, renderables, new Terrain2D[0][], FirstOracleConstants.INDEX_ZERO_2I ) );
        
        cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
        cameraController.updateMovableCamera2D( camera2D );
        cameraController.addCameraListener( ( event, source ) -> cameraController.updateMovableCamera2D( camera2D ) );
        
        sceneProvider = () -> renderedScene;
        window = WindowBuilder.simpleWindow( settings, sceneProvider ).build();
        
        window.addTimeListener( cameraController );
        
        window.addQuitListener( cameraController );
        window.addKeyListener( cameraController );
        window.addMouseListener( cameraController );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                camera2D.forceUpdate();
            }
        } );
        cameraController.start();
        
        window.run();
    }
    
}
