/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.jfxgl;
import com.firststory.firstoracle.FrameworkProvider;
import com.firststory.firstoracle.Runner;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.LWJGLDebug;
import javafx.application.Application;
import org.lwjgl.system.Callback;

import static com.firststory.firstoracle.FirstOracleConstants.isDebugMode;

/**
 * Class used to create JFXGL context.
 * For now it can only create one instance for one window as there is no support in JFXGL.
 */
public class JfxglContext implements FrameworkProvider {
    private static JfxglContext instance;
    private static Callback debugCallback;
    
    public static JfxglContext createInstance(long hwnd, String[] args, Application app) {
        if(app == null)
            return new DummyContext();
        if(instance != null)
            throw new RuntimeException( "Multiple JFXGL instances not supported." );
        if( isDebugMode() )
            debugCallback = LWJGLDebug.enableDebugging();
        instance = new JfxglContext();
        Runner.registerFramework( instance );
        JFXGL.start( hwnd, new String[]{}, app );
        return instance;
    }
    
    @Override
    public void terminate() {
        if( debugCallback != null)
            debugCallback.free();
        JFXGL.terminate();
    }
    
    private JfxglContext(){}
    
    public void render() {
        JFXGL.render();
    }
    
    private static class DummyContext extends JfxglContext{
        private DummyContext() {}
        @Override
        public void render() {}
    }
}
