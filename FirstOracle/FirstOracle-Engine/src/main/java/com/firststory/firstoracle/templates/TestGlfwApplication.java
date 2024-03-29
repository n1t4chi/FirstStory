/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.controller.*;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.window.*;

import java.util.*;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class TestGlfwApplication {
    
    private static final Index3D TERRAIN_3D_DIM = Index3D.id3( 0,0,0 );
    private static final Index2D TERRAIN_2D_DIM = Index2D.id2( 2,2 );
    private static final int DEFAULT_SCENE = 0;
    
    public static void main( String[] args ) throws Exception {
        new TestGlfwApplication().run();
    }
    private RegistrableWindow window;
    private CameraController cameraController;
    private WindowSettings settings;
    
    public void run() throws Exception {
        var width = 300;
        var height = 300;
        settings = WindowSettings.builder().setVerticalSync( false )
            .setResizeable( true )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build();
        
        var texture2D = Texture.create( "resources/First Oracle/texture2D.png" );
        var texture3D_1 = Texture.create( "resources/First Oracle/texture3Dc.png" );
        var texture3D_2 = Texture.create( "resources/First Oracle/texture3D.png" );
        var textureTerrain2D = Texture.create( "resources/First Oracle/textureGrid2D.png" );
        var textureTerrain3D = Texture.create( "resources/First Oracle/texture3DHEX2.png" );
        var compoundTexture = Texture.createCompound( "resources/First Oracle/compound/tex_#frame#_#direction#.png",
            4,
            6
        );
        var bgTex = Texture.create( "resources/First Oracle/background.png" );
        var ovTex = Texture.create( "resources/First Oracle/overlay.png" );
    
        var bg = new DirectableRectangle();
        bg.setTexture( bgTex );
    
        var ov = new DirectableRectangle();
        ov.setTexture( ovTex );
        ov.getTransformations().setPosition( 0, -0.5f );
        ov.getTransformations().setScale( 1f, 0.5f );
    
        var rectangle = new DirectableRectangle();
        rectangle.setTexture( texture2D );
        rectangle.getTransformations().setPosition( 0, 4);
        
        var rectangleCompound = new FullyAnimatedRectangle();
        
        var frameController = new LoopedFrameController();
        var directionController = new DefaultDirectionController( compoundTexture.getDirections() );
        
        rectangleCompound.setDirectionController( directionController );
        frameController.setCurrentState( compoundTexture.getFrames(), 0, 1 );
        
        rectangleCompound.setFrameController( frameController );
        rectangleCompound.getTransformations().setPosition( 4, 4 );
        rectangleCompound.setTexture( compoundTexture );
        
        var cube1 = new StaticCube();
        cube1.setTexture( texture3D_1 );
        cube1.getTransformations().setPosition( 0, 0, 0 );
    
        var cube2 = new StaticCube();
        cube2.setTexture( texture3D_2 );
        cube2.getTransformations().setPosition( 4, 0, 0 );
    
        var emptyHex = new StaticHexPrism();
        emptyHex.setTexture( FirstOracleConstants.EMPTY_TEXTURE );
        emptyHex.getTransformations().setPosition( 8, 0, 0 );

    
        var terrain2D = new StaticRectangleGrid();
        terrain2D.setTexture( textureTerrain2D );
        
        var terrains2D = new RectangleGrid[ TERRAIN_2D_DIM.x() ][ TERRAIN_2D_DIM.y() ];
        for ( var x = 0; x < terrains2D.length; x++ ) {
            for ( var y = 0; y < terrains2D[ x ].length; y++ ) {
                terrains2D[ x ][ y ] = terrain2D;
            }
        }
        
        var terrain3D = new StaticCubeGrid();
        terrain3D.setTexture( textureTerrain3D );
        
        var terrains3D = new StaticCubeGrid[ TERRAIN_3D_DIM.x() ][ TERRAIN_3D_DIM.y() ][ TERRAIN_3D_DIM.z() ];
        for ( var x = 0; x < terrains3D.length; x++ ) {
            for ( var y = 0; y < terrains3D[ x ].length; y++ ) {
                for ( var z = 0; z < terrains3D[ x ][ y ].length; z++ ) {
                    terrains3D[ x ][ y ][ z ] = terrain3D;
                }
            }
        }

        List< PositionableObject3D< ?, ? > > renderables3D = Arrays.asList( cube1, cube2, emptyHex );
        List< PositionableObject2D< ?, ? > > renderables2D = Arrays.asList( rectangle, rectangleCompound );
    
        window = WindowBuilder.registrableWindow( settings ).build();
    
        cameraController = CameraController.createAndStart( window, settings, CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
        
        window.createNewScene( DEFAULT_SCENE, TERRAIN_2D_DIM, FirstOracleConstants.INDEX_ZERO_2I, TERRAIN_3D_DIM, FirstOracleConstants.INDEX_ZERO_3I );
        
        window.deregisterMultipleObjects2D( DEFAULT_SCENE, renderables2D );
        window.registerMultipleObjects3D( DEFAULT_SCENE, renderables3D );
    
        window.registerMultipleTerrains3D( DEFAULT_SCENE, terrains3D );
        window.registerMultipleTerrains2D( DEFAULT_SCENE, terrains2D );
    
        window.setBackgroundColour( DEFAULT_SCENE, FirstOracleConstants.WHITE );
        window.registerBackground( DEFAULT_SCENE, bg );
        window.registerOverlay( DEFAULT_SCENE, ov );
        window.setScene2DCamera( DEFAULT_SCENE, cameraController.getCamera2D() );
        window.setScene3DCamera( DEFAULT_SCENE, cameraController.getCamera3D() );
    
        var fpsCounter = new FpsCounter( window );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                fpsCounter.setTextPosition( 0, event.getHeight()/2-7 );
            }
        } );
        window.registerOverlay( DEFAULT_SCENE, fpsCounter );
        
        window.run();
    }
    
}
