/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.rendering.RenderingFramework;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class RenderLoopPerformanceTester extends RenderLoopDelegate {
    private static final Logger performanceLogger = Logger.getLogger( Window.class.getName() + "#performance" );
    
    RenderLoopPerformanceTester( RenderLoop delegate ) {
        super( delegate );
    }
    
    @Override
    public void loopInside( Window window, RenderingFramework renderingFramework ) throws Exception {
        performanceLogger.finest( window + ": loop cycle start" );
        super.loopInside( window, renderingFramework );
        performanceLogger.finest( window + ": loop cycle end" );
    }
    
    @Override
    public void render( Window window, RenderingFramework renderingFramework ) throws Exception {
        performanceLogger.finest( window + ": loop render start" );
        super.render( window, renderingFramework );
        performanceLogger.finest( window + ": loop render start" );
    }
}
