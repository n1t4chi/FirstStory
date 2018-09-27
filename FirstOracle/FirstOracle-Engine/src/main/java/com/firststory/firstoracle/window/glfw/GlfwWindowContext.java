/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.glfw;

import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.window.glfw.GlfwFramework.deregisterWindow;


/**
 * @author n1t4chi
 */
public class GlfwWindowContext implements WindowContext {
    private static final GeneralJoystickCallback JOYSTICK_CALLBACK;
    static {
        JOYSTICK_CALLBACK = new GeneralJoystickCallback();
        GLFW.glfwSetJoystickCallback( JOYSTICK_CALLBACK );
    }
    private final long address;
    private final KeyCallback keyCallback = new KeyCallback();
    private final MouseButtonCallback mouseButtonCallback = new MouseButtonCallback();
    private final MouserPositionCallback mousePositionCallback = new MouserPositionCallback();
    private final MouserScrollCallback mouseScrollCallback = new MouserScrollCallback();
    private final WindowPositionCallback windowPositionCallback = new WindowPositionCallback();
    private final WindowSizeCallback windowSizeCallback = new WindowSizeCallback();
    private final List<KeyListener > keyListeners = new ArrayList<>();
    private final List<JoystickListener > joystickListeners = new ArrayList<>();
    private final List<MouseListener > mouseListeners = new ArrayList<>();
    private final List<WindowListener > windowListeners = new ArrayList<>();
    private final WindowFocusCallback windowFocusCallback = new WindowFocusCallback();
    private final WindowCloseCallback windowCloseCallback = new WindowCloseCallback();
    private final boolean isOpenGLWindow;
    private boolean verticalSyncEnabled = false;
    
    /**
     * Constructor for glfw window wrapper.
     * @param windowID window ID generated by GLFW window, cannot be less than 0.
     * @param isOpenGLWindow whether should use openGL specific calls in methods or not
     */
    GlfwWindowContext( long windowID, boolean isOpenGLWindow ) {
        if(windowID <= 0)
            throw new IllegalArgumentException("WindowID cannot be 0 or less.");
        this.address = windowID;
        this.isOpenGLWindow = isOpenGLWindow;
        setupCallbacks();
    }
    
    @Override
    public void setVerticalSync( boolean enabled ) {
        this.verticalSyncEnabled = enabled;
    }
    
    @Override
    public long getAddress() {
        return address;
    }
    
    @Override
    public void show() {
        GLFW.glfwShowWindow( address );
    }
    
    private boolean destroyed = false;
    
    @Override
    public void destroy(){
        if( !destroyed ) {
            destroyed = true;
            JOYSTICK_CALLBACK.removeWindow( this );
            deregisterWindow( this );
            Callbacks.glfwFreeCallbacks( address );
            GLFW.glfwDestroyWindow( address );
        }
    }
    
    @Override
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose( address );
    }

    @Override
    public void quit() {
        GLFW.glfwSetWindowShouldClose( address, true );
    }

    @Override
    public void addKeyListener( KeyListener listener ) {
        keyListeners.add( listener );
    }

    @Override
    public void addMouseListener( MouseListener listener ) {
        mouseListeners.add( listener );
    }

    @Override
    public void addWindowListener( WindowListener listener ) {
        windowListeners.add( listener );
    }

    @Override
    public void addJoystickListener( JoystickListener listener ) {
        joystickListeners.add( listener );
    }
    
    @Override
    public void setUpSingleRender() {
        setWindowToCurrentThread();
        setupVerticalSync();
    }

    @Override
    public void tearDownSingleRender() {
        if( isOpenGLWindow ) {
            GLFW.glfwSwapBuffers( address );
        }
        GLFW.glfwPollEvents();
    }

    @Override
    public void setupVerticalSync() {
        if( !isOpenGLWindow ) {
            return;
        }
        if ( verticalSyncEnabled ) {
            GLFW.glfwSwapInterval( 1 );
        } else {
            GLFW.glfwSwapInterval( 0 );
        }
    }
    
    @Override
    public void setWindowToCurrentThread() {
        if( isOpenGLWindow ) {
            GLFW.glfwMakeContextCurrent( address );
        }
    }
    
    void notifyJoystickListeners( int jid, int event ) {
        var joystickEvent = new JoystickEvent(GlfwWindowContext.this, jid, event );
        joystickListeners.forEach( listener -> listener.notify( joystickEvent ) );
    }
    
    private void setupCallbacks(){
        GLFW.glfwSetKeyCallback( address, keyCallback );
        GLFW.glfwSetMouseButtonCallback( address, mouseButtonCallback );
        GLFW.glfwSetScrollCallback( address, mouseScrollCallback );
        GLFW.glfwSetCursorPosCallback( address, mousePositionCallback );
        GLFW.glfwSetWindowPosCallback( address, windowPositionCallback );
        GLFW.glfwSetWindowSizeCallback( address, windowSizeCallback );
        GLFW.glfwSetWindowFocusCallback( address, windowFocusCallback );
        GLFW.glfwSetWindowCloseCallback( address, windowCloseCallback );
        JOYSTICK_CALLBACK.addWindow( this );
    }
    
    private class MouserScrollCallback implements GLFWScrollCallbackI {
        @Override
        public void invoke( long window, double xoffset, double yoffset ) {
            var event = new MouseScrollEvent( GlfwWindowContext.this,xoffset, yoffset );
            mouseListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class MouseButtonCallback implements GLFWMouseButtonCallbackI {
        @Override
        public void invoke( long window, int button, int action, int mods ) {
            var event = new MouseButtonEvent( GlfwWindowContext.this, button, action, mods );
            mouseListeners.forEach( listener -> listener.notify( event ) );
        }
    }

    private class MouserPositionCallback implements GLFWCursorPosCallbackI {
        @Override
        public void invoke( long window, double xpos, double ypos ) {
            var event = new MousePositionEvent( GlfwWindowContext.this, xpos, ypos );
            mouseListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class KeyCallback implements GLFWKeyCallbackI {
        @Override
        public void invoke( long window, int key, int scancode, int action, int mods ) {
            var event = new KeyEvent(
                GlfwWindowContext.this, GlfwKeyMap.parseKeyCode( key, scancode, action, mods )
            );
            keyListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class WindowSizeCallback implements GLFWWindowSizeCallbackI {
        @Override
        public void invoke( long window, int width, int height ) {
            var event = new WindowSizeEvent( GlfwWindowContext.this, width, height );
            windowListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class WindowPositionCallback implements GLFWWindowPosCallbackI {
        @Override
        public void invoke( long window, int xpos, int ypos ) {
            var event = new WindowPositionEvent( GlfwWindowContext.this, xpos, ypos );
            windowListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class WindowCloseCallback implements GLFWWindowCloseCallbackI {
        @Override
        public void invoke( long window ) {
            var event = new WindowCloseEvent( GlfwWindowContext.this );
            windowListeners.forEach( listener -> listener.notify( event ) );
        }
    }
    
    private class WindowFocusCallback implements GLFWWindowFocusCallbackI {
        @Override
        public void invoke( long window, boolean focused ) {
            var event = new WindowFocusedEvent( GlfwWindowContext.this, focused );
            windowListeners.forEach( listener -> listener.notify( event ) );
        }
    }
}

