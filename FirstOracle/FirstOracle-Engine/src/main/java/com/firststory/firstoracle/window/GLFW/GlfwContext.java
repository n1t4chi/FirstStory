/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

import com.firststory.firstoracle.WindowSettings;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author n1t4chi
 */
public class GlfwContext{
    private static Set<GlfwWindow> instances = new HashSet<>();
    private static GlfwContext instance;
    private static GLFWErrorCallback errorCallback;
    
    public synchronized static GlfwContext createInstance() {
        if(instance == null){
            setUpErrorCallback();
            init();
            instance = new GlfwContext();
        }
        return instance;
    }
    
    public synchronized static void terminate() {
        for ( GlfwWindow window : instances ) {
            window.destroy();
        }
        GLFW.glfwTerminate();
        freeErrorCallback();
        instance = null;
    }
    
    private static void registerWindow( GlfwWindow window ) {
        instances.add( window );
    }
    
    static void deregisterWindow( GlfwWindow window ) {
        instances.remove( window );
    }
    
    private static void init() {
        if ( !GLFW.glfwInit() ) {
            throw new IllegalStateException( "Unable to initialize GLFW!" );
        }
    }
    
    private static void setUpErrorCallback() {
        errorCallback = GLFWErrorCallback.createPrint( System.err ).set();
    }
    
    private static void freeErrorCallback() {
        if ( errorCallback != null ) {
            errorCallback.free();
        }
    }
    
    private GlfwContext(){}

    public synchronized GlfwWindow createWindow(WindowSettings settings){
        setWindowHints(settings);
    
        long monitor;
        if(settings.getMonitorIndex() <= 0 ){
            monitor = GLFW.glfwGetPrimaryMonitor();
        }else{
            PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
            if(settings.getMonitorIndex() > pointerBuffer.capacity()){
                throw new MonitorIndexOutOfBoundException(settings.getMonitorIndex(),pointerBuffer.capacity());
            }
            monitor = pointerBuffer.get( settings.getMonitorIndex()-1 );
        }
        
        GLFWVidMode mode = GLFW.glfwGetVideoMode( monitor );
    
        // Create the window
        int width = settings.getWidth();
        int height = settings.getHeight();
        width = ( width > 0 ) ? width : mode.width();
        height = ( height > 0 ) ? height : mode.height();
        settings.setWidth( width );
        settings.setHeight( height );
        long windowId;
        switch ( settings.getWindowMode() ) {
            case FULLSCREEN:
                GLFW.glfwWindowHint( GLFW.GLFW_RED_BITS, mode.redBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_GREEN_BITS, mode.greenBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_BLUE_BITS, mode.blueBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_REFRESH_RATE, mode.refreshRate() );
                break;
            case BORDERLESS:
                GLFW.glfwWindowHint( GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE );
            case WINDOWED:
            default:
                monitor = NULL;
                break;
        }
        
        windowId = GLFW.glfwCreateWindow(
            width,
            height,
            settings.getTitle(),
            monitor,
            NULL
        );
        if ( windowId == NULL ) {
            throw new CannotCreateWindowException();
        }
        int[] left = new int[ 1 ];
        int[] top = new int[ 1 ];
        int[] right = new int[ 1 ];
        int[] bottom = new int[ 1 ];
    
        GLFW.glfwGetWindowFrameSize( windowId, left, top, right, bottom );
        if ( settings.getPositionX() != 0 || settings.getPositionY() != 0 ) {
            GLFW.glfwSetWindowPos( windowId,
                settings.getPositionX() + left[ 0 ],
                settings.getPositionY() + top[ 0 ]
            );
        }
        GlfwWindow window = new GlfwWindow( windowId );
        window.setWindowToCurrentThread();
        window.setupVerticalSync( settings.isVerticalSync() );
        registerWindow(window);
        return window;
    }
    
    private void setWindowHints(WindowSettings settings) {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint( GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3 );
        GLFW.glfwWindowHint( GLFW.GLFW_CONTEXT_VERSION_MINOR, 2 );
        GLFW.glfwWindowHint( GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE );
        GLFW.glfwWindowHint( GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE );
        GLFW.glfwWindowHint( GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE );
        GLFW.glfwWindowHint(
            GLFW.GLFW_RESIZABLE,
            ( settings.isResizeable() ) ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE
        );
        GLFW.glfwWindowHint( GLFW.GLFW_SAMPLES, settings.getAntiAliasing() );
    }
    
    public double getTime() {
        return GLFW.glfwGetTime();
    }
}
