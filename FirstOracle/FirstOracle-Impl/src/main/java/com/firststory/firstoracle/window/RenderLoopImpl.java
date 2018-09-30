/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.rendering.RenderingFramework;

/**
 * @author n1t4chi
 */
public class RenderLoopImpl extends RenderLoop {
    
    @Override
    public void loopInside( Window window, RenderingFramework renderingFramework ) throws Exception {
        setupCycle( window );
        render( window, renderingFramework );
    }
    
    @Override
    public void render( Window window, RenderingFramework renderingFramework ) throws Exception {
        renderingFramework.invoke( instance -> invokedRender( window, instance ) );
    }
    
    private void invokedRender( Window window, RenderingFramework instance ) {
        window.invokedRender( instance );
    }
    
    @Override
    public void setupCycle( Window window ) {
        window.setupRenderCycleVariables();
    }
}
