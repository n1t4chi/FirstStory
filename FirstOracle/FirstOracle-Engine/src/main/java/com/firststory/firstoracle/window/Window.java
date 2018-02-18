/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.CheckSupport;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.rendering.WindowRenderingContext;
import com.firststory.firstoracle.window.GLFW.GlfwContext;
import com.firststory.firstoracle.window.notifying.*;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.LWJGLDebug;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
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
    QuitNotifier {
    
    private static Window instance = null;
    
    public static synchronized Window getInstance(
        WindowSettings windowSettings,
        Application application,
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
        RenderingContext renderer
    ) {
        if ( instance == null ) {
            instance = new Window( windowSettings,
                application,
                shaderProgram2D,
                shaderProgram3D,
                renderer
            );
        }
        return instance;
    }
    
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< WindowResizedListener > sizeListeners = new ArrayList<>( 3 );
    private final ArrayList< WindowMovementListener > movementListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final Application application;
    private final ShaderProgram2D shaderProgram2D;
    private final ShaderProgram3D shaderProgram3D;
    private final RenderingContext renderer;
    private long windowID = -1;
    
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
            GlfwContext glfw = GlfwContext.getInstance();
            this.windowID = glfw.createWindow(settings);
            
            setOpenGlContectToCurrentThread();
            if ( !openGLSupportedEnough() ) {
                throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
            }
            enableFunctionality();
            setupCallbacks();
            shaderProgram2D.compile();
            shaderProgram3D.compile();
            renderer.init();
        } catch ( Exception ex ) {
            System.err.println( "Exception:\n" + ex );
            ex.printStackTrace();
            close();
            throw new RuntimeException( ex );
        }
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName( "Window" );
        setVisible();
        Callback debugProc = LWJGLDebug.enableDebugging();
        JFXGL.start( windowID, new String[]{}, application );
        try {
            loop();
            notifyQuitListeners();
            sleep( 10 );
        } catch ( Exception ex ) {
            System.err.println( "Exception:\n" + ex );
            ex.printStackTrace();
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
        return !GLFW.glfwWindowShouldClose( windowID );
    }
    
    /**
     * Notifies window that it should stop rendering and close itself.
     * <p>
     * If this method is overridden then also {@link #shouldWindowClose()} needs to be overridden
     * or there will be inconsistencies.
     */
    public void quit() {
        GLFW.glfwSetWindowShouldClose( windowID, true );
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
    
    public void addKeyCallbackController( GLFWKeyCallback controller ) {
        GLFW.glfwSetKeyCallback( windowID, controller );
    }
    
    public void addMouseScrollCallbackController( GLFWScrollCallback controller ) {
        GLFW.glfwSetScrollCallback( windowID, controller );
    }
    
    public void addMouseButtonCallbackController( GLFWMouseButtonCallback controller ) {
        GLFW.glfwSetMouseButtonCallback( windowID, controller );
    }
    
    public void addMousePositionCallbackController( GLFWCursorPosCallback controller ) {
        GLFW.glfwSetCursorPosCallback( windowID, controller );
    }
    
    private void setOpenGlContectToCurrentThread() {
        GLFW.glfwMakeContextCurrent( windowID );
        GL.createCapabilities();
        
    }
    
    private boolean openGLSupportedEnough() {
        return CheckSupport.checkSupport();
    }
    
    private void enableFunctionality() {
        if ( settings.isVerticalSync() ) {
            GLFW.glfwSwapInterval( 1 );
        } else {
            GLFW.glfwSwapInterval( 0 );
        }
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glEnable( GL11.GL_TEXTURE_2D );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glFrontFace( GL11.GL_CCW );
        ARBVertexArrayObject.glBindVertexArray( ARBVertexArrayObject.glGenVertexArrays() );
    }
    
    private void setupCallbacks() {
        GLFW.glfwSetWindowSizeCallback( windowID, ( window, width, height ) -> {
            GL11.glViewport( 0, 0, width, height );
            settings.setWidth( width );
            settings.setHeight( height );
            notifySizeListeners( width, height );
        } );
        GLFW.glfwSetWindowPosCallback( windowID, ( window, xpos, ypos ) -> notifyMovementListeners( xpos, ypos ) );
    }
    
    private void close() {
        destroyWindow();
    }
    
    private void destroyWindow() {
        if ( windowID > 0 ) {
            Callbacks.glfwFreeCallbacks( windowID );
            GLFW.glfwDestroyWindow( windowID );
        }
    }
    
    private void setVisible() {
        GLFW.glfwShowWindow( windowID );
    }
    
    private void loop() {
        while ( shouldWindowClose() ) {
            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
            notifyTimeListener( GLFW.glfwGetTime() );
            renderer.render();
            JFXGL.render();
            GLFW.glfwSwapBuffers( windowID );
            GLFW.glfwPollEvents();
        }
    }
}
