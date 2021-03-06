/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public interface Renderer {
    
    void init();
    
    void dispose();
    
    void render( RenderingContext renderingContext, double currentRenderTime );
}
