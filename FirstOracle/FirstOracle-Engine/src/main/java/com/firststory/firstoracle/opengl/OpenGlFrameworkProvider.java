/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.Runner;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.window.WindowContext;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create {@link OpenGlFramework} in safe manner and possibly other methods that do not require
 * context acquiring and releasing between threads.
 */
public class OpenGlFrameworkProvider implements RenderingFrameworkProvider {
    
    private static final OpenGlFrameworkProvider framework = new OpenGlFrameworkProvider();
    static {
        Runner.registerFramework( framework );
    }
    
    private final Map<Thread,OpenGlFramework > instances = new HashMap<>();
    
    public static OpenGlFrameworkProvider getProvider() {
        return framework;
    }
    
    @Override
    public synchronized OpenGlFramework getRenderingFramework( WindowContext window ) {
        return instances.computeIfAbsent( Thread.currentThread(), thread -> new OpenGlFramework() );
    }
    @Override
    public void terminate() {}
    
    @Override
    public boolean isOpenGL() {
        return true;
    }
    
    private OpenGlFrameworkProvider() {}
}
