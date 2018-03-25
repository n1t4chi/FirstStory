/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.notifying.WindowListener;
import com.firststory.firstoracle.window.notifying.WindowSizeEvent;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class OpenGlAndGlfwApplication2D {
    
    public static void main( String[] args ) throws Exception{
        new OpenGlAndGlfwApplication2D().run( args );
    }
    private Window window;
    private WindowRenderingContext renderer;
    private SceneProvider sceneProvider;
    private CameraController cameraController;
    private RenderedSceneMutable renderedScene;
    private Grid3DRenderer grid3DRenderer;
    private Grid2DRenderer grid2DRenderer;
    private ShaderProgram2D shaderProgram2D;
    private ShaderProgram3D shaderProgram3D;
    private WindowSettings settings;
    
    public void run( String[] args ) throws Exception{
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
        shaderProgram3D = new ShaderProgram3D();
        shaderProgram2D = new ShaderProgram2D();
//        grid2DRenderer = new DummyGrid2DRenderer();
        grid2DRenderer = new BoundedPositiveGrid2DRenderer( shaderProgram2D, 20, 30, 10 );
//            Grid2DRenderer grid2DRenderer = new BoundedGrid2DRenderer( shaderProgram2D,
//                100,
//                10,
//                1
//            );
        grid3DRenderer = new DummyGrid3DRenderer();

        renderedScene = new RenderedSceneMutable( settings );
        Texture texture1 = new Texture( "resources/First Oracle/texture2D.png" );
        Texture texture2 = new Texture( "resources/First Oracle/obj.png" );
        Texture compundTexture = Texture.createCompoundTexture( "resources/First Oracle/compound/tex_#frame#_#direction#.png",
            4,
            6
        );
        NonAnimatedRectangleGrid terrain = new NonAnimatedRectangleGrid();
        terrain.setTexture( texture1 );

        NonAnimatedRectangle object = new NonAnimatedRectangle();
        object.setTransformations( new Mutable2DTransformations() );
        object.setTexture( texture2 );

        DirectionController directionController = new DefaultDirectionController( compundTexture.getDirections() );
        LoopedFrameController frameController = new LoopedFrameController();
        AnimatedRectangle compound = new AnimatedRectangle();
        frameController.setCurrentState( compundTexture.getFrames(), 0, 1 );

        compound.setDirectionController( directionController );
        compound.setFrameController( frameController );
        compound.setUvMap( new PlaneUvMap( compundTexture ) );
        compound.setTexture( compundTexture );
        compound.setTransformations( new Mutable2DTransformations() );
        compound.getTransformations().setPosition( -4, -4 );
        compound.getTransformations().setScale( 4, 4 );


        RectangleGrid[][] array = new RectangleGrid[20][20];

        for ( int x = 0; x < array.length; x++ ) {
            for ( int y = 0; y < array[x].length; y++ ) {
                array[x][y] = terrain;
            }
        }
        renderedScene.setScene2D( ( renderer ) -> {
            renderer.renderAll( array );
            renderer.render( object );
            renderer.render( compound );
        } );
        cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
        cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
        cameraController.addCameraListener( ( event, source ) -> cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene
            .getCamera2D() ) );
        
        sceneProvider = () -> renderedScene;
        renderer = new WindowRenderingContext(
            shaderProgram2D,
            shaderProgram3D,
            grid2DRenderer,
            grid3DRenderer,
            sceneProvider,
            settings.isUseTexture(),
            settings.isDrawBorder()
        );
        window = Window.getOpenGlInstance( settings, shaderProgram2D, shaderProgram3D, renderer );
        window.init();
        renderedScene.setBackgroundColour( new Vector4f( 1, 1, 1, 1 ) );
    
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
