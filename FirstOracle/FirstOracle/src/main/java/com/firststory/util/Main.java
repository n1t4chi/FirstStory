/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.util;

import com.firststory.firstoracle.window.*;
import com.firststory.firstoracle.camera.IsometricCamera;
import com.firststory.firstoracle.window.shader.ShaderProgram;
import cuchaz.jfxgl.JFXGLLauncher;
import javafx.scene.control.Label;

/**
 * Main class that initialises whole test application.
 *
 * @author n1t4chi
 */
public class Main {


    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( Main.class, args );
    }

    public static void jfxglmain( String[] args ) {
        WindowSettings settings = new WindowSettings.WindowSettingsBuilder()
            .setVerticalSync( false )
            .build();
        ShaderProgram shaderProgram = new ShaderProgram();
        SceneProvider sceneProvider = () -> {
            RenderedScene renderedScene = new RenderedScene();
            renderedScene.setCamera( new IsometricCamera( 10, 0, 0, 0, 0.5f, 30, 30, 1 ) );
            return renderedScene;
        };
        GridRenderer gridRenderer = GridRenderer.DummyGridRenderer;
        Renderer renderer = new Renderer( shaderProgram, gridRenderer, sceneProvider );

        WindowApplication application = new WindowApplication( overlayPanel -> {
            overlayPanel.getChildren().add( new Label( "Hello World!" ) );
        } );
        Window window = Window.getInstance( settings, application, shaderProgram, renderer );
        window.init();
        Thread graphicThread = new Thread( window );
        graphicThread.run();
    }
}
