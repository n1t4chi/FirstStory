/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public class DummyRenderer implements RenderingContext {
    
    public static final RenderingContext DUMMY_RENDERER = new DummyRenderer();
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void render( double currentRenderTime ) {
    }
}
