/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create {@link OpenGlInstance} in safe manner and possibly other methods that do not require
 * context aquiring and releasing between threads.
 */
public class OpenGlContext {
    private final static Map<Thread,OpenGlInstance> instances = new HashMap<>();
    
    public synchronized static OpenGlInstance createInstance() {
        return instances.computeIfAbsent( Thread.currentThread(), thread -> new OpenGlInstance() );
    }
    public static void terminate() {}
    
    private OpenGlContext() {}
}
