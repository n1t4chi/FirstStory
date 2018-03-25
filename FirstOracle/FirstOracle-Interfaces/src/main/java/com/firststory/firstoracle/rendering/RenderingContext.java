/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object.VertexAttributeLoader;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    void init();
    
    void dispose();
    
    void render( VertexAttributeLoader loader, double currentRenderTime );
}
