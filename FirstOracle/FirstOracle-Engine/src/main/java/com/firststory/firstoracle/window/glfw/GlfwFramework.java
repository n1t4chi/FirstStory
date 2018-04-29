/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.glfw;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.window.WindowFramework;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author n1t4chi
 */
public class GlfwFramework implements WindowFramework {
    private static Set<GlfwWindowContext > instances = new HashSet<>();
    
    private static void registerWindow( GlfwWindowContext window ) {
        instances.add( window );
    }
    
    static void deregisterWindow( GlfwWindowContext window ) {
        instances.remove( window );
    }
    
    GlfwFramework(){}

    @Override
    public synchronized GlfwWindowContext createWindowContext( WindowSettings settings, boolean isOpenGLWindow ){
        setWindowHints(settings);
        if( !isOpenGLWindow ) {
            setNonOpenGlWindowHints();
        }
    
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
        GlfwWindowContext window = new GlfwWindowContext( windowId );
        registerWindow(window);
        window.setVerticalSync( settings.isVerticalSync() );
        
        if( isOpenGLWindow ){
            window.setWindowToCurrentThread();
        }
        
        return window;
    }
    
    private void setNonOpenGlWindowHints() {
        GLFW.glfwWindowHint( GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API );
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
    
    @Override
    public double getTime() {
        return GLFW.glfwGetTime();
    }
    
    @Override
    public void destroy() {}
}