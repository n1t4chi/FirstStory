/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.notyfying.WindowListener;
import com.firststory.firstoracle.notyfying.WindowSizeEvent;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.RectangleGrid;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.NonAnimatedCubeGrid;
import com.firststory.firstoracle.object3D.NonAnimatedPlane3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedScene2D;
import com.firststory.firstoracle.scene.RenderedScene3D;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.Window;
import org.joml.Vector2ic;
import org.joml.Vector3ic;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class TestGlfwApplication {
    
    public static void main( String[] args ) throws Exception {
        new TestGlfwApplication().run();
    }
    
    private Window window;
    private WindowRenderer renderer;
    private SceneProvider sceneProvider;
    private CameraController cameraController;
    private RenderedSceneMutable renderedScene;
    private Grid3DRenderer grid3DRenderer;
    private Grid2DRenderer grid2DRenderer;
    private WindowSettings settings;
    
    public void run() throws Exception{
        int width = 300;
        int height = 300;
        settings = new WindowSettings.WindowSettingsBuilder()
            .setVerticalSync( false )
            .setResizeable( true )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .setDrawBorder( true )
            .build();
        grid2DRenderer = new DummyGrid2DRenderer();
//        grid2DRenderer = new BoundedPositiveGrid2DRenderer( 5, 5, 2 );
//        Grid2DRenderer grid2DRenderer = new BoundedGrid2DRenderer(
//            100,
//            10,
//            1
//        );
//        grid3DRenderer = new DummyGrid3DRenderer();
        grid3DRenderer = new BoundedGrid3DRenderer( 100, 25, 5 );

        renderedScene = new RenderedSceneMutable( settings );
        Texture texture1 = new Texture( "resources/First Oracle/texture3D.png" );
        Texture texture2 = new Texture( "resources/First Oracle/texture3Dc.png" );
        Texture texture3 = new Texture( "resources/First Oracle/texture3DHEX.png" );
        Texture texture4 = new Texture( "resources/First Oracle/texture3DHEX2.png" );
//        texture1 = texture2 = texture3 = texture4 = FirstOracleConstants.EMPTY_TEXTURE;
//        Texture compundTexture = Texture.createCompoundTexture( "resources/First Oracle/compound/tex_#frame#_#direction#.png",
//            4,
//            6
//        );
        NonAnimatedPlane3D cube1 = new NonAnimatedPlane3D();
        cube1.setTexture( texture1 );
        cube1.getTransformations().setPosition( 0,0,0 );
    
        NonAnimatedPlane3D cube2 = new NonAnimatedPlane3D();
        cube2.setTexture( texture2 );
        cube2.getTransformations().setPosition( 4,0,0 );

        NonAnimatedPlane3D hex1 = new NonAnimatedPlane3D();
        hex1.setTexture( texture3 );
        hex1.getTransformations().setPosition( 8,0,0 );

        NonAnimatedPlane3D hex2 = new NonAnimatedPlane3D();
        hex2.setTexture( texture4 );
        hex2.getTransformations().setPosition( 12,0,0 );
    
        NonAnimatedPlane3D hex3 = new NonAnimatedPlane3D();
        hex3.setTexture( FirstOracleConstants.EMPTY_TEXTURE );
        hex3.getTransformations().setPosition( 4,4,0 );
        
//        NonAnimatedRectangle object = new NonAnimatedRectangle();
//        object.setTransformations( new Mutable2DTransformations() );
//        object.setTexture( texture2 );
//
//        DirectionController directionController = new DefaultDirectionController( compundTexture.getDirections() );
//        LoopedFrameController frameController = new LoopedFrameController();
//        AnimatedRectangle compound = new AnimatedRectangle();
//        frameController.setCurrentState( compundTexture.getFrames(), 0, 1 );
//
//        compound.setDirectionController( directionController );
//        compound.setFrameController( frameController );
//        compound.setUvMap( new PlaneUvMap( compundTexture ) );
//        compound.setTexture( compundTexture );
//        compound.setTransformations( new Mutable2DTransformations() );
//        compound.getTransformations().setPosition( -4, -4 );
//        compound.getTransformations().setScale( 4, 4 );
    
        RectangleGrid[][] array = new RectangleGrid[0][0];
    
        NonAnimatedCubeGrid grid = new NonAnimatedCubeGrid();
        grid.setTexture( texture1 );
        
        NonAnimatedCubeGrid[][][] terrain3DS = new NonAnimatedCubeGrid[0][0][0];
        for ( int x = 0; x < terrain3DS.length; x++ ) {
            for ( int y = 0; y < terrain3DS[x].length; y++ ) {
                for ( int z = 0; z < terrain3DS[x][y].length; z++ ) {
                    terrain3DS[ x ][ y ][ z ] = grid;
                }
            }
        }

//        for ( int x = 0; x < array.length; x++ ) {
//            for ( int y = 0; y < array[x].length; y++ ) {
//                array[x][y] = terrain;
//            }
//        }
//        List<Renderable> renderables = Arrays.<Renderable>asList( compound, object );
        List<PositionableObject3D> renderables =
//            Collections.emptyList();
            Arrays.asList(
                cube1,
                cube2,
                hex1,
                hex2,
                hex3
            );
        
        renderedScene.setScene3D( ( new RenderedScene3D() {
            @Override
            public Terrain3D[][][] getTerrains() {
                return terrain3DS;
            }
    
            @Override
            public Collection< PositionableObject3D > getObjects() {
                return renderables;
            }
    
            @Override
            public Vector3ic getTerrainShift() {
                return FirstOracleConstants.VECTOR_ZERO_3I;
            }
        } ) );
        renderedScene.setScene2D( new RenderedScene2D() {
            @Override
            public Terrain2D[][] getTerrains() {
                return array;
            }
    
            @Override
            public Collection< PositionableObject2D > getObjects() {
                return Collections.emptyList();
//                return renderables;
            }
    
            @Override
            public Vector2ic getTerrainShift() {
                return FirstOracleConstants.VECTOR_ZERO_2I;
            }
        } );
        
        cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
        cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
        cameraController.updateIsometricCamera3D( renderedScene.getCamera3D() );
        cameraController.addCameraListener(
            ( event, source ) -> {
                cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
                cameraController.updateIsometricCamera3D( renderedScene.getCamera3D() );
            }
        );
        
        sceneProvider = () -> renderedScene;
        renderer = new WindowRenderer(
            grid2DRenderer,
            grid3DRenderer,
            sceneProvider,
            settings.isUseTexture(),
            settings.isDrawBorder()
        );
        window = Window.build( settings, renderer ).build();
        window.init();
        renderedScene.setBackgroundColour( new Vector4f( 1f, 1f, 1f, 1f ) );
    
        window.addTimeListener( cameraController );
        
        window.addQuitListener( cameraController );
        window.addKeyListener( cameraController );
        window.addMouseListener( cameraController );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                renderedScene.getCamera2D().forceUpdate();
                renderedScene.getCamera3D().forceUpdate();
            }
        } );
        cameraController.start();
    
        window.run();
    }
    
}
