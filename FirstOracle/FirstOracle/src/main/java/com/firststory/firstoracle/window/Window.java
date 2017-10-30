/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.CheckSupport;
import com.firststory.firstoracle.window.shader.ShaderProgram;
import cuchaz.jfxgl.JFXGL;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Class window that creates JavaFX with possible OpenGL rendering in background provided using
 * {@link Renderer} and {@link SceneProvider}
 * <p>
 * Window creates {@link Application} internally and provides overlay panel {@link Pane}
 * on which all overlay components can be placed onto.
 * <p>
 * For Window to work the application needs to run main method with code below:
 * <code>
 * public static void main(String[] args) {
 * JFXGLLauncher.launchMain(&lt;class with jfxlmain&gt;.class, args);
 * }
 * <code/>
 * Also there needs to be method jfxglmain(String[] args) (whose class is referenced in main above)
 * and only from there you can create any objects or threads, Window included.
 * <code>
 * public static void jfxglmain(String[] args) {
 * &lt;create all threads and windows here&gt;
 * };
 * <code/>
 * Also Window needs to be initialised in this order:
 * init();
 * //any OpenGL context can be used after
 * run(); <- starts rendering loop so it's advised to put it into new thread
 *
 * @author: n1t4chi
 */
public class Window implements Runnable{

    private static Window instance;

    public static synchronized Window getInstance(
        WindowSettings windowSettings,
        Application application,
        ShaderProgram shaderProgram,
        Renderer renderer
    )
    {
        return instance = new Window( windowSettings, application, shaderProgram, renderer );
    }

    private final WindowSettings windowSettings;
    private long windowID = -1;
    private GLFWErrorCallback errorCallback;
    private Application application;
    private ShaderProgram shaderProgram;
    private Renderer renderer;

    public Window(
        WindowSettings windowSettings,
        Application application, ShaderProgram shaderProgram,
        Renderer renderer
    ) {
        this.windowSettings = windowSettings;
        this.application = application;
        this.shaderProgram = shaderProgram;
        this.renderer = renderer;
    }

    public void init() {
        try {
            setUpErrorCallback();
            initOpenGL();
            setWindowHints();
            initWindow();
            setOpenGlContectToCurrentThread();
            if ( !openGLSupportedEnough() ) {
                throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
            }
            enableFunctionality();
            shaderProgram.compile();
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
        setVisible();
        JFXGL.start( windowID, new String[]{}, application );
        try {
            loop();
        } catch ( Exception ex ) {
            System.err.println( "Exception:\n" + ex );
            ex.printStackTrace();
            throw new RuntimeException( ex );
        } finally {
            close();
        }
    }

    private void loop() {
        while ( !GLFW.glfwWindowShouldClose( windowID ) ) {
            JFXGL.render();
            GLFW.glfwSwapBuffers( windowID );
            GLFW.glfwPollEvents();
        }
    }

    private void close() {
        try {
            destroyWindow();
        } finally {
            terminateGL();
            freeErrorCallback();
        }
    }

    private void setVisible() { GLFW.glfwShowWindow( windowID ); }

    private void enableFunctionality()
    {
        if ( windowSettings.isVerticalSync() ) {
            GLFW.glfwSwapInterval( 1 );
        } else {
            GLFW.glfwSwapInterval( 0 );
        }
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glFrontFace( GL11.GL_CCW );
    }

    private void setOpenGlContectToCurrentThread() {
        GLFW.glfwMakeContextCurrent( windowID );
        GL.createCapabilities();

    }

    private void initWindow() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode mode = GLFW.glfwGetVideoMode( monitor );
        // Create the window
        int width = windowSettings.getWidth();
        int height = windowSettings.getHeight();
        width = ( width > 0 ) ? width : mode.width();
        height = ( height > 0 ) ? height : mode.height();
        windowSettings.setWidth( width );
        windowSettings.setHeight( height );
        long window;

        switch ( windowSettings.getWindowMode() ) {
            case WINDOWED_FULLSCREEN:
                GLFW.glfwWindowHint( GLFW.GLFW_RED_BITS, mode.redBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_GREEN_BITS, mode.greenBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_BLUE_BITS, mode.blueBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_REFRESH_RATE, mode.refreshRate() );
            case FULLSCREEN:
                window = GLFW.glfwCreateWindow(
                    width,
                    height,
                    windowSettings.getTitle(),
                    monitor,
                    NULL
                );
                break;
            case BORDERLESS:
                GLFW.glfwWindowHint( GLFW.GLFW_DECORATED, GL11.GL_FALSE );
            case WINDOWED:
            default:
                window = GLFW.glfwCreateWindow(
                    width,
                    height,
                    windowSettings.getTitle(),
                    NULL,
                    NULL
                );
                break;
        }
        if ( window == NULL ) {
            throw new RuntimeException( "Failed to create the GLFW window" );
        }
        this.windowID = window;
        int[] left = new int[1];
        int[] top = new int[1];
        int[] right = new int[1];
        int[] bottom = new int[1];

        GLFW.glfwGetWindowFrameSize( window, left, top, right, bottom );
        if ( windowSettings.getPositionX() >= 0 && windowSettings.getPositionY() >= 0 ) {
            GLFW.glfwSetWindowPos(
                window,
                windowSettings.getPositionX() + left[0],
                windowSettings.getPositionY() + top[0]
            );
        }
    }

    private void setWindowHints() {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint( GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3 );
        GLFW.glfwWindowHint( GLFW.GLFW_CONTEXT_VERSION_MINOR, 2 );
        GLFW.glfwWindowHint( GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE );
        GLFW.glfwWindowHint( GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE );
        GLFW.glfwWindowHint( GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE );
        GLFW.glfwWindowHint( GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE );
        GLFW.glfwWindowHint( GLFW.GLFW_SAMPLES, windowSettings.getAntiAliasing() );
    }

    private void destroyWindow() {
        if ( windowID > 0 ) {
            Callbacks.glfwFreeCallbacks( windowID );
            GLFW.glfwDestroyWindow( windowID );
        }
    }

    private void terminateGL() {
        JFXGL.terminate();
        GLFW.glfwTerminate();
    }

    private void setUpErrorCallback() {
        errorCallback = GLFWErrorCallback.createPrint( System.err ).set();
    }

    private void freeErrorCallback() {
        if ( errorCallback != null ) { errorCallback.free(); }
    }

    private boolean openGLSupportedEnough() { return CheckSupport.checkSupport(); }

    private void initOpenGL() {
        if ( !GLFW.glfwInit() ) {
            throw new IllegalStateException( "Unable to initialize GLFW!" );
        }
    }
}
