/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.rendering.WindowRenderingContext;
import com.firststory.firstoracle.window.GLFW.GlfwContext;
import com.firststory.firstoracle.window.GLFW.GlfwWindow;
import com.firststory.firstoracle.window.OpenGL.OpenGlContext;
import com.firststory.firstoracle.window.notifying.*;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.LWJGLDebug;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.Callback;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Thread.sleep;

/**
 * Class window that creates JavaFX with possible OpenGL rendering in background provided using
 * {@link WindowRenderingContext} and {@link com.firststory.firstoracle.rendering.SceneProvider}
 * <p>
 * Window creates {@link Application} internally and provides overlay panel {@link Pane}
 * on which all overlay components can be placed onto.
 * <p>
 * For Window to work the application needs to run main method with code below:
 * <code>
 * public static void main(String[] args) {
 * JFXGLLauncher.launchMain(&lt;class with jfxlmain&gt;.class, args);
 * }
 * </code>
 * Also there needs to be method jfxglmain(String[] args) (whose class is referenced in main above)
 * and only from there you can create any objects or threads, Window included.
 * <code>
 * public static void jfxglmain(String[] args) {
 * &lt;create all threads and windows here&gt;
 * };
 * </code>
 * Also Window needs to be initialised in this order:
 * init();
 * //any OpenGL context can be used after
 * run(); &gt;- starts rendering loop so it's advised to put it into new thread
 *
 * @author n1t4chi
 */
public class Window implements Runnable,
    TimeNotifier,
    WindowResizedNotifier,
    WindowMovementNotifier,
    QuitNotifier
{
    
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< WindowResizedListener > sizeListeners = new ArrayList<>( 3 );
    private final ArrayList< WindowMovementListener > movementListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final Application application;
    private final ShaderProgram2D shaderProgram2D;
    private final ShaderProgram3D shaderProgram3D;
    private final RenderingContext renderer;
    private GlfwWindow window;
    private GlfwContext glfw;
    private OpenGlContext openGl;
    
    public Window(
        WindowSettings windowSettings,
        Application application,
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
        RenderingContext renderer
    ) {
        this.settings = windowSettings;
        this.application = application;
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.renderer = renderer;
    }
    
    
    
    public void init() {
        try {
            glfw = GlfwContext.getInstance();
            window = glfw.createWindow( settings );
            
            window.setWindowToCurrentThread();
            window.setupVerticalSync( settings.isVerticalSync() );
            
            OpenGlContext.getInstance();
            setupCallbacks();
            shaderProgram2D.compile();
            shaderProgram3D.compile();
            renderer.init();
        } catch ( Exception ex ) {
            close();
            throw new RuntimeException( ex );
        }
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName( "Window" );
        window.show();
        Callback debugProc = LWJGLDebug.enableDebugging();
        JFXGL.start( window.getID(), new String[]{}, application );
        try {
            loop();
            notifyQuitListeners();
            sleep( 10 );
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        } finally {
            debugProc.free();
            close();
        }
    }
    
    /**
     * Returns whether the window should close before next rendering cycle.
     * <p>
     * If this method is overridden then also {@link #quit()} needs to be overridden
     * or there will be inconsistencies.
     *
     * @return true when window should close
     */
    public boolean shouldWindowClose() {
        return window.shouldClose();
    }
    
    /**
     * Notifies window that it should stop rendering and close itself.
     * <p>
     * If this method is overridden then also {@link #shouldWindowClose()} needs to be overridden
     * or there will be inconsistencies.
     */
    public void quit() {
        window.quit();
    }
    
    @Override
    public Collection< TimeListener > getTimeListeners() {
        return timeListeners;
    }
    
    @Override
    public Collection< QuitListener > getQuitListeners() {
        return quitListeners;
    }
    
    @Override
    public Collection< WindowMovementListener > getMovementListeners() {
        return movementListeners;
    }
    
    @Override
    public Collection< WindowResizedListener > getSizeListeners() {
        return sizeListeners;
    }
    
    public void addKeyListener( GLFWKeyCallback controller ) {
        window.setKeyCallback( controller );
    }
    
    public void addMouseScrollListener( GLFWScrollCallback controller ) {
        window.setMouseScrollCallback( controller );
    }
    
    public void addMouseButtonListener( GLFWMouseButtonCallback controller ) {
        window.setMouseButtonCallback( controller );
    }
    
    public void addMousePositionCallbackController( GLFWCursorPosCallback controller ) {
        window.setMousePositionCallback( controller );
    }
    
    private void setupCallbacks() {
        window.setSizeCallback( ( window, width, height ) -> {
            OpenGlContext.updateViewPort( 0, 0, width, height );
            settings.setWidth( width );
            settings.setHeight( height );
            notifySizeListeners( width, height );
        } );
        window.setPositionCallback( ( window, xpos, ypos ) -> notifyMovementListeners( xpos, ypos ) );
    }
    
    private void close() {
        window.destroy();
    }
    
    private void loop() {
        while ( !shouldWindowClose() ) {
            window.setUpRenderLoop();
            OpenGlContext.clearScreen();
            notifyTimeListener( glfw.getTime() );
            renderer.render();
            JFXGL.render();
            window.cleanAfterLoop();
        }
    }
}
