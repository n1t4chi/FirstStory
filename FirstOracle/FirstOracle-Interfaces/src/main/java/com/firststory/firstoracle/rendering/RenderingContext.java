/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    void init();
    
    void dispose();
    
    void render( double currentRenderTime );
}
