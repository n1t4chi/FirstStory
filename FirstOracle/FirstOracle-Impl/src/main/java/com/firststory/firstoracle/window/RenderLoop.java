/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.rendering.RenderingFramework;

/**
 * @author n1t4chi
 */
public abstract class RenderLoop {
    
    void loop( Window window, RenderingFramework renderingFramework ) throws Exception {
        while ( !window.shouldWindowClose() ) {
            loopInside( window, renderingFramework );
        }
    }
    
    abstract void loopInside( Window window, RenderingFramework renderingFramework ) throws Exception;
    
    abstract void render( Window window, RenderingFramework renderingFramework ) throws Exception;
    
    abstract void setupCycle( Window window );
}
