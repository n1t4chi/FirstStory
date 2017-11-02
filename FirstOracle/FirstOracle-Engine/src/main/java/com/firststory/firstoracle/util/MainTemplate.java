/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.util;

import com.firststory.firstoracle.camera.camera2D.MovableCamera2D;
import com.firststory.firstoracle.rendering.RenderedScene;
import com.firststory.firstoracle.rendering.SceneProvider;
import com.firststory.firstoracle.window.*;
import com.firststory.firstoracle.camera.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.window.shader.ShaderProgram;
import com.firststory.firstoracle.window.SceneRenderer3D;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGLLauncher;
import javafx.scene.control.Label;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 * @author n1t4chi
 */
public class MainTemplate {


    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( MainTemplate.class, args );
    }

    public static void jfxglmain( String[] args ) {
        WindowSettings settings = new WindowSettings.WindowSettingsBuilder()
            .setVerticalSync( false )
            .build();
        ShaderProgram3D shaderProgram3D = new ShaderProgram3D();
        ShaderProgram2D shaderProgram2D = new ShaderProgram2D();
        SceneProvider sceneProvider = () -> {
            RenderedScene renderedScene = new RenderedScene();
            renderedScene.setIsometricCamera3D( new IsometricCamera3D( 100, 0, 0, 0, 0.5f, 0, 0, 1 ) );
            renderedScene.setCamera2D( new MovableCamera2D( 1,0,0,1,0 ) );
            renderedScene.setBackgroundColour( new Vector4f( 1,1,1,1 ) );
            return renderedScene;
        };
        GridRenderer3D gridRenderer = new GridRenderer3D(shaderProgram3D,100,25 , 5);
        SceneRenderer3D renderer = new SceneRenderer3D(
            shaderProgram2D
            , shaderProgram3D
            , gridRenderer
            , sceneProvider
        );

        WindowApplication application = new WindowApplication( overlayPanel -> {
            overlayPanel.getChildren().add( new Label( "Hello World!" ) );
        });
        Window window = Window.getInstance( settings, application, shaderProgram2D, shaderProgram3D, renderer );
        window.init();
        Thread graphicThread = new Thread( window );
        graphicThread.run();
    }

}
