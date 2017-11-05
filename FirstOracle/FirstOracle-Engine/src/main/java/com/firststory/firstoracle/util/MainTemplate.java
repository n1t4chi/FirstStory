/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.util;

import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.RectangleGrid;
import com.firststory.firstoracle.object3D.HexPrismGrid;
import com.firststory.firstoracle.rendering.Object2DRenderer;
import com.firststory.firstoracle.rendering.Object3DRenderer;
import com.firststory.firstoracle.rendering.SceneProvider;
import com.firststory.firstoracle.scene.RenderedObjects2D;
import com.firststory.firstoracle.scene.RenderedObjects3D;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.*;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGLLauncher;
import cuchaz.jfxgl.controls.OpenGLPane;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.joml.Vector3i;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class MainTemplate {

    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( MainTemplate.class, args );
    }

    public static void jfxglmain( String[] args ) {
        WindowSettings settings = new WindowSettings.WindowSettingsBuilder().setVerticalSync( false )
            .build();
        ShaderProgram3D shaderProgram3D = new ShaderProgram3D();
        ShaderProgram2D shaderProgram2D = new ShaderProgram2D();
        GridRenderer gridRenderer = new GridRenderer( shaderProgram3D, 100, 25, 5 );

        RenderedSceneMutable renderedScene = new RenderedSceneMutable();
        renderedScene.setIsometricCamera3D( new IsometricCamera3D( 10, 0, 0, 0, 0.5f, 0, 0, 1 ) );
        renderedScene.setCamera2D( new MovableCamera2D( 1, 0, 0, 1, 0 ) );
        renderedScene.setBackgroundColour( new Vector4f( 0, 1, 0, 1 ) );

        Vector4f colour = new Vector4f( 0, 0, 0, 0 );
        float maxFloat = 1;


        try {
            RectangleGrid overlay = new RectangleGrid(
                new Texture( "resources/First Oracle/grid.png" )
            );
            renderedScene.setOverlay( new RenderedObjects2D() {
                @Override
                public void render( Object2DRenderer renderer ) {
                    renderer.render( overlay, colour, maxFloat );
                }
            } );

            HexPrismGrid terrain1 = new HexPrismGrid(new Texture( "resources/First Oracle/texture3DHEX2.png" ));
            HexPrismGrid terrain2 = new HexPrismGrid(new Texture( "resources/First Oracle/texture3DHEX.png" ));

            HexPrismGrid[][][] array = new HexPrismGrid[10][20][20];
            for ( int y = 0; y < 10; y++ ) {
                for ( int x = 0; x < 20; x++ ) {
                    for ( int z = 0; z < 20; z++ )
                    { array[y][x][z] = ( ( x + z ) % 2 == 0 ) ? terrain1 : terrain2; }
                }
            }

            renderedScene.setScene3D( new RenderedObjects3D() {
                @Override
                public void render( Object3DRenderer renderer ) {
                    Vector3i arrayShift = new Vector3i( 0, 0,0 );
                    for ( int y = 0; y < 10; y++ ) {
                        for ( int x = 0; x < 20; x++ ) {
                            for ( int z = 0; z < 20; z++ ) {
                                renderer.render(
                                    array[y][x][z],
                                    array[y][x][z].computePosition( x,y,z, arrayShift ),
                                    colour,
                                    maxFloat
                                );
                            }
                        }
                    }
                }
            } );

            SceneProvider sceneProvider = () -> {
                return renderedScene;
            };
            SceneRenderer renderer = new SceneRenderer( shaderProgram2D,
                shaderProgram3D,
                gridRenderer,
                sceneProvider,
                true,
                true
            );

            OverlayContentManager contentManager = new OverlayContentManager() {
                Label fpsLabel;
                Label timeLabel;
                BorderPane overlayPanel;
                @Override
                public Pane createOverlayPanel() {
                    return overlayPanel = new BorderPane();
                }

                @Override
                public void init() {
                    fpsLabel = new Label(  );
                    timeLabel = new Label(  );
                    overlayPanel.setTop( fpsLabel );
                    overlayPanel.setBottom( timeLabel );
                }

                @Override
                public void update( double currentTime, int currentFps ) {
                    fpsLabel.setText( "FPS:"+currentFps );
                    timeLabel.setText( "Time:"+currentTime );
                }
            };

            WindowApplication application = new WindowApplication(contentManager);
            renderer.addObserver( application );
            Window window = Window.getInstance( settings,
                application,
                shaderProgram2D,
                shaderProgram3D,
                renderer
            );
            window.init();
            Thread graphicThread = new Thread( window );
            graphicThread.run();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
