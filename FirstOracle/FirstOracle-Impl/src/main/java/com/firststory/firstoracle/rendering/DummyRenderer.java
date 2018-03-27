/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public class DummyRenderer implements Renderer {
    
    public static final Renderer DUMMY_RENDERER = new DummyRenderer();
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void render(
        RenderingContext renderingContext, double currentRenderTime
    ) {}
}
