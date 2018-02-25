/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.JFXGL;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.LWJGLDebug;
import javafx.application.Application;
import org.lwjgl.system.Callback;

/**
 * Class used to create JFXGL context.
 * For now it can only create one instance for one window as there is no support in JFXGL.
 */
public class JfxglContext {
    private static JfxglContext instance = null;
    
    
    public static Callback getDebugCallback() {
        return LWJGLDebug.enableDebugging();
    }
    
    public static JfxglContext createInstance(long hwnd, String[] args, Application app) {
        if(instance != null)
            throw new RuntimeException( "Multiple JFXGL instances not supported." );
        instance = new JfxglContext();
        JFXGL.start( hwnd, new String[]{}, app );
        return instance;
    }
    
    private JfxglContext(){}
    
    public static void terminate() {
        JFXGL.terminate();
    }
    
    public void render() {
        JFXGL.render();
    }
}
