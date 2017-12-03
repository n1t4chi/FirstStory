/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.OverlayContentManager;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.WindowApplication;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGLLauncher;
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
public class MainTemplate2D {
    
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
    
    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( MainTemplate2D.class, args );
    }
    
    public static void jfxglmain( String[] args ) {
        try {
            int width = 300;
            int height = 300;
            settings = new WindowSettings.WindowSettingsBuilder().setVerticalSync( false )
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
                3,
                6
            );
            RectangleGrid terrain = new RectangleGrid();
            terrain.setTexture( texture1 );
            Rectangle< Mutable2DTransformations > object = new Rectangle<>();
            object.setTransformations( new Mutable2DTransformations() );
    
            AnimatedMutableObject2D< Mutable2DTransformations, Plane2DVertices > compund = new AnimatedMutableObject2D<>();
    
            compund.setVertices( Plane2DVertices.getPlane2DVertices( -2, 2, -2, 2 ) );
            compund.setUvMap( new PlaneUvMap( compundTexture.getFrames(),
                compundTexture.getDirections(),
                compundTexture.getRows(),
                compundTexture.getColumns()
            ) );
            compund.setTexture( compundTexture );
            compund.setTransformations( new Mutable2DTransformations() );
            compund.getTransformations().setPosition( -4, -4 );
            compund.getTransformations().setScale( 2, 2 );
    
            object.setTexture( texture2 );
    
            RectangleGrid[][] array = new RectangleGrid[20][20];
            
            for ( int x = 0; x < array.length; x++ ) {
                for ( int y = 0; y < array[x].length; y++ ) {
                    array[x][y] = terrain;
                }
            }
            renderedScene.setScene2D( ( objectRenderer, terrainRenderer ) -> {
                terrainRenderer.renderAll( array );
                objectRenderer.render( object );
                objectRenderer.render( compund );
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
            window = Window.getInstance( settings, application, shaderProgram2D, shaderProgram3D, renderer );
            window.init();
            renderer.addFpsListeners( application );
            window.addTimeListener( application );
            renderedScene.setBackgroundColour( new Vector4f( 1, 1, 1, 1 ) );
    
            window.addQuitListener( cameraController );
            window.addKeyCallbackController( cameraController.getKeyCallback() );
            window.addMouseScrollCallbackController( cameraController.getScrollCallback() );
            window.addSizeListener( ( newWidth, newHeight, source ) -> {
                renderedScene.getCamera2D().forceUpdate();
                renderedScene.getCamera3D().forceUpdate();
            } );
            
            Thread cameraControllerThread = new Thread( cameraController );
            cameraControllerThread.start();
    
            window.run();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
