/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
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
import com.firststory.firstoracle.window.OverlayContentManager;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.WindowApplication;
import com.firststory.firstoracle.window.notifying.WindowListener;
import com.firststory.firstoracle.window.notifying.WindowSizeEvent;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.joml.Vector2fc;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class FullApplication2D {
    
    private static Window window;
    private static OverlayContentManager contentManager;
    private static WindowRenderingContext renderer;
    private static SceneProvider sceneProvider;
    private static CameraController cameraController;
    private static RenderedSceneMutable renderedScene;
    private static Grid3DRenderer grid3DRenderer;
    private static BoundedPositiveGrid2DRenderer grid2DRenderer;
    private static ShaderProgram2D shaderProgram2D;
    private static ShaderProgram3D shaderProgram3D;
    private static WindowSettings settings;
    private static WindowApplication application;
    
    public static void main( String[] args ) throws Exception{
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
        contentManager = new OverlayContentManager() {
            float x = 0;
            float y = 0;
            Label fpsLabel;
            Label timeLabel;
            Label mouseLabel;
            BorderPane pane;

            @Override
            public Pane createOverlayPanel() {
                return pane = new BorderPane();
            }

            @Override
            public void init( Stage stage, Scene scene ) {
                fpsLabel = new Label();
                timeLabel = new Label();
                mouseLabel = new Label();
                pane.setTop( fpsLabel );
                pane.setBottom( timeLabel );
                pane.setLeft( mouseLabel );
                
                pane.addEventFilter( MouseEvent.MOUSE_MOVED, event -> {
                    Vector2fc translated = renderedScene.getCamera2D()
                        .translatePointOnScreen( ( float ) event.getX(),
                            ( float ) event.getY(),
                            settings.getWidth(),
                            settings.getHeight()
                        );
                    x = translated.x();
                    y = translated.y();
                    object.getTransformations().setPosition( x, y );
                    mouseLabel.setText( "mouse: (" + x + ", " + y + ")" );
                } );
                scene.addEventFilter( KeyEvent.KEY_TYPED, event -> {
                    System.err.println( "typed:" + event.getCharacter() );
                    switch ( event.getCharacter() ) {
                        case "n":
                            grid2DRenderer.setGridWidth( grid2DRenderer.getGridWidth() - 1 );
                            break;
                        case "m":
                            grid2DRenderer.setGridWidth( 2 * grid2DRenderer.getGridWidth() + 1 );
                            break;
                        case "k":
                            grid2DRenderer.setGridHeight( grid2DRenderer.getGridHeight() - 1 );
                            break;
                        case "l":
                            grid2DRenderer.setGridHeight( 2 * grid2DRenderer.getGridHeight() + 1 );
                            break;
                        case "o":
                            grid2DRenderer.setIntermediateAxesStep( grid2DRenderer.getIntermediateAxesStep() - 1 );
                            break;
                        case "p":
                            grid2DRenderer.setIntermediateAxesStep( grid2DRenderer.getIntermediateAxesStep() + 1 );
                            break;
                    }
                } );
            }

            @Override
            public void update( double currentTime, int currentFps ) {
                fpsLabel.setText( "FPS:" + currentFps );
                timeLabel.setText( String.format( "current time: %.1fs", currentTime ) );
            }
        };
        application = new WindowApplication( contentManager );
        window = Window.getOpenGlWithJavaFxInstance( settings, application, shaderProgram2D, shaderProgram3D, renderer );
        window.init();
        renderer.addFpsListener( application );
        window.addTimeListener( application );
        renderedScene.setBackgroundColour( new Vector4f( 1, 1, 1, 1 ) );

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
