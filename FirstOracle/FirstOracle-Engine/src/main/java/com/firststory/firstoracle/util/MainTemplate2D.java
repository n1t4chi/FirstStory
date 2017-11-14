/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.util;

import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.RectangleGrid;
import com.firststory.firstoracle.object3D.CubeGrid;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedObjects2D;
import com.firststory.firstoracle.scene.RenderedObjects3D;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.OverlayContentManager;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.WindowApplication;
import com.firststory.firstoracle.window.WindowSettings;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGLLauncher;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class MainTemplate2D {

    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( MainTemplate2D.class, args );
    }

    public static void jfxglmain( String[] args ) {
        try {
            int width = 1000;
            int height = 800;
            WindowSettings settings = new WindowSettings.WindowSettingsBuilder().setVerticalSync(
                false )
                .setResizeable( true )
                .setWidth( width )
                .setHeight( height )
                .setDrawBorder( true )
                .build();
            ShaderProgram3D shaderProgram3D = new ShaderProgram3D();
            ShaderProgram2D shaderProgram2D = new ShaderProgram2D();
            Grid2DRenderer grid2DRenderer = new BoundedGrid2DRenderer( shaderProgram2D,
                100,
                10,
                1
            );
            Grid3DRenderer grid3DRenderer = new DummyGrid3DRenderer();

            RenderedSceneMutable renderedScene = new RenderedSceneMutable();

            Vector4f colour = new Vector4f( 0, 0, 0, 0 );
            float maxFloat = 1;
            RectangleGrid overlay = new RectangleGrid( new Texture(
                "resources/First Oracle/grid.png" ) );
            renderedScene.setOverlay( new RenderedObjects2D() {
                @Override
                public void render( Object2DRenderer renderer ) { }
            } );

            RectangleGrid terrain1 = new RectangleGrid( new Texture(
                "resources/First Oracle/texture2D.png" ) );

            RectangleGrid[][] array = new RectangleGrid[20][20];

            for ( int x = 0; x < array.length; x++ ) {
                for ( int y = 0; y < array[x].length; y++ ) {
                    array[x][y] = terrain1;
                }
            }
            renderedScene.setScene2D( new RenderedObjects2D() {
                @Override
                public void render( Object2DRenderer renderer )
                {
                    Vector2i arrayShift = new Vector2i( 0, 0 );
                    for ( int x = 0; x < array.length; x++ ) {
                        for ( int y = 0; y < array[x].length; y++ ) {
                            renderer.render( array[x][y],
                                array[x][y].computePosition( x, y, arrayShift ),
                                colour,
                                maxFloat
                            );
                        }
                    }
                }
            } );
            CameraController cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(),
                10,
                1f
            );
            SceneProvider sceneProvider = () -> {
                cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
                return renderedScene;
            };
            SceneRenderer renderer = new SceneRenderer( shaderProgram2D,
                shaderProgram3D,
                grid2DRenderer,
                grid3DRenderer,
                sceneProvider,
                true,
                true
            );
            OverlayContentManager contentManager = new OverlayContentManager() {
                @Override
                public Pane createOverlayPanel() { return new Pane(); }

                @Override
                public void init() {}

                @Override
                public void update( double v, int i ) {}
            };
            WindowApplication application = new WindowApplication( contentManager );
            renderer.addFpsObserver( application );
            Window window = Window.getInstance( settings,
                application,
                shaderProgram2D,
                shaderProgram3D,
                renderer
            );
            window.init();
            renderedScene.setCamera2D( new MovableCamera2D( 1,
                0,
                0,
                ( ( float ) settings.getHeight() ) / settings.getWidth(),
                0
            ) );
            renderedScene.setBackgroundColour( new Vector4f( 1, 1, 1, 1 ) );

            window.addQuitObserver( cameraController );
            window.addKeyCallbackController( cameraController.getKeyCallback() );
            window.addMouseScrollCallbackController( cameraController.getScrollCallback() );

            Thread cameraControllerThread = new Thread( cameraController );
            cameraControllerThread.start();

            window.run();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
