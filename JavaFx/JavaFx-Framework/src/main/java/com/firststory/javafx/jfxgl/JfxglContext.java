/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.javafx.jfxgl;

import com.firststory.firstoracle.window.GuiFramework;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.LWJGLDebug;
import javafx.application.Application;
import org.lwjgl.system.Callback;

import static com.firststory.firstoracle.PropertiesUtil.isDebugMode;

/**
 * Class used to create JFXGL context.
 * For now it can only create one instance for one window as there is no support in JFXGL.
 */
public class JfxglContext implements GuiFramework {
    private static JfxglContext instance;
    private static Callback debugCallback;
    
    public static JfxglContext createInstance(long hwnd, String[] args, Application app) {
        if(app == null)
            throw new RuntimeException( "Application cannot be null!" );
        if(instance != null)
            throw new RuntimeException( "Multiple JFXGL instances not supported." );
        if( isDebugMode() )
            debugCallback = LWJGLDebug.enableDebugging();
        instance = new JfxglContext();
        JFXGL.start( hwnd, new String[]{}, app );
        return instance;
    }
    
    public void terminate() {
        if( debugCallback != null)
            debugCallback.free();
        JFXGL.terminate();
    }
    
    private JfxglContext(){}
    
    public void render() {
        JFXGL.render();
    }
}
