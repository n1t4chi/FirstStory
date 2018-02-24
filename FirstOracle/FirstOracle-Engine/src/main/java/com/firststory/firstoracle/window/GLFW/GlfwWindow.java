/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.GLFW;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class GlfwWindow {
    private final long windowID;
    
    public long getID() {
        return windowID;
    }
    
    /**
     * Constructor for glfw window wrapper.
     * @param windowID window ID generated by GLFW window, cannot be less than 0.
     */
    GlfwWindow( long windowID ) {
        if(windowID <= 0)
            throw new IllegalArgumentException("WindowID cannot be 0 or less.");
        this.windowID = windowID;
    }
    
    public void show() {
        GLFW.glfwShowWindow( windowID );
    }
    
    public void destroy(){
        Callbacks.glfwFreeCallbacks( windowID );
        GLFW.glfwDestroyWindow( windowID );
    }
    
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose( windowID );
    }
    
    public void quit() {
        GLFW.glfwSetWindowShouldClose( windowID, true );
    }
    
    public void setKeyCallback( GLFWKeyCallback callback ) {
        GLFW.glfwSetKeyCallback( windowID, callback );
    }
    
    public void setMouseScrollCallback( GLFWScrollCallback callback ) {
        GLFW.glfwSetScrollCallback( windowID, callback );
    }
    
    public void setMouseButtonCallback( GLFWMouseButtonCallback callback ) {
        GLFW.glfwSetMouseButtonCallback( windowID, callback );
    }
    
    public void setMousePositionCallback( GLFWCursorPosCallback callback ) {
        GLFW.glfwSetCursorPosCallback( windowID, callback );
    }
    
    public void setPositionCallback( GLFWWindowPosCallbackI callback ) {
        GLFW.glfwSetWindowPosCallback( windowID, callback );
    }
    
    public void setSizeCallback( GLFWWindowSizeCallbackI callback ) {
        GLFW.glfwSetWindowSizeCallback( windowID, callback );
    }
    
    public void setUpRenderLoop() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }
    
    public void cleanAfterLoop() {
        GLFW.glfwSwapBuffers( windowID );
        GLFW.glfwPollEvents();
    }
    
    public void setOpenGlContextToCurrentThread() {
        GLFW.glfwMakeContextCurrent( windowID );
        GL.createCapabilities();
    }
    
    public void setupVerticalSync( boolean verticalSync ) {
        if ( verticalSync ) {
            GLFW.glfwSwapInterval( 1 );
        } else {
            GLFW.glfwSwapInterval( 0 );
        }
    }
}
