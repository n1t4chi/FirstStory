/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import java.io.IOException;

public interface RenderingFramework {
    
    void updateViewPort( int x, int y, int width, int height );
    
    void invoke( FrameworkCommands commands ) throws Exception;
    
    void compileShaders() throws IOException;
    
    void dispose();
    
    void render( Renderer renderer, double lastFrameUpdate );
}
