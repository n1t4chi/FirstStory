/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.rendering.RenderingFramework;

/**
 * @author n1t4chi
 */
public class RenderLoopDelegate extends RenderLoop {
    
    private final RenderLoop delegate;
    
    RenderLoopDelegate( RenderLoop delegate ) {
        this.delegate = delegate;
    }
    
    @Override
    public void loopInside( Window window, RenderingFramework renderingFramework ) throws Exception {
        delegate.loopInside( window, renderingFramework );
    }
    
    @Override
    public void render( Window window, RenderingFramework renderingFramework ) throws Exception {
        delegate.render( window, renderingFramework );
    }
    
    @Override
    public void setupCycle( Window window ) {
        delegate.setupCycle( window );
    }
}
