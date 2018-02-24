/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

import com.firststory.firstoracle.WindowSettings;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.system.MemoryUtil.NULL;

public class GlfwContext {
    
    private static GlfwContext instance = new GlfwContext();
    private static GLFWErrorCallback errorCallback;
    
    static {
        setUpErrorCallback();
        init();
    }
    
    public static GlfwContext getInstance() {
        return instance;
    }
    
    public static void terminate() {
        GLFW.glfwTerminate();
        freeErrorCallback();
        instance = null;
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

    public GlfwWindow createWindow(WindowSettings settings){
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
        long window;
        switch ( settings.getWindowMode() ) {
            case FULLSCREEN:
                GLFW.glfwWindowHint( GLFW.GLFW_RED_BITS, mode.redBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_GREEN_BITS, mode.greenBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_BLUE_BITS, mode.blueBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_REFRESH_RATE, mode.refreshRate() );
                break;
            case BORDERLESS:
                GLFW.glfwWindowHint( GLFW.GLFW_DECORATED, GL11.GL_FALSE );
            case WINDOWED:
            default:
                monitor = NULL;
                break;
        }
        
        window = GLFW.glfwCreateWindow(
            width,
            height,
            settings.getTitle(),
            monitor,
            NULL
        );
        if ( window == NULL ) {
            throw new CannotCreateWindowException();
        }
        long windowId = window;
        int[] left = new int[ 1 ];
        int[] top = new int[ 1 ];
        int[] right = new int[ 1 ];
        int[] bottom = new int[ 1 ];
    
        GLFW.glfwGetWindowFrameSize( window, left, top, right, bottom );
        if ( settings.getPositionX() != 0 || settings.getPositionY() != 0 ) {
            GLFW.glfwSetWindowPos( window,
                settings.getPositionX() + left[ 0 ],
                settings.getPositionY() + top[ 0 ]
            );
        }
        return new GlfwWindow(windowId);
    }
    
    public void setWindowHints(WindowSettings settings) {
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
