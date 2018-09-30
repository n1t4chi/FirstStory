/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.glfw;

import com.firststory.firstoracle.Runner;
import com.firststory.firstoracle.window.WindowFramework;
import com.firststory.firstoracle.window.WindowFrameworkProvider;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * @author n1t4chi
 */
public class GlfwFrameworkProvider implements WindowFrameworkProvider {
    private static GlfwFrameworkProvider instance;
    private static GLFWErrorCallback errorCallback;
    
    public synchronized static GlfwFrameworkProvider getProvider() {
        if(instance == null){
            setUpErrorCallback();
            init();
            instance = new GlfwFrameworkProvider();
            Runner.registerFramework( instance );
        }
        return instance;
    }
    
    private GlfwFrameworkProvider() {
    }
    
    @Override
    public synchronized void terminate() {
        GLFW.glfwTerminate();
        freeErrorCallback();
        instance = null;
    }
    
    @Override
    public WindowFramework getWindowFramework() {
        return new GlfwFramework();
    }
    
    @Override
    public boolean isGlfw() {
        return true;
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
}
