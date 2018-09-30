/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.rendering.RenderingFramework;

/**
 * @author n1t4chi
 */
public class RenderLoopEarlyExit extends RenderLoopDelegate {
    
    RenderLoopEarlyExit( RenderLoop delegate ) {
        super( delegate );
    }
    
    @Override
    public void loopInside( Window window, RenderingFramework renderingFramework ) throws Exception {
        super.loopInside( window, renderingFramework );
        window.quit();
    }
}
