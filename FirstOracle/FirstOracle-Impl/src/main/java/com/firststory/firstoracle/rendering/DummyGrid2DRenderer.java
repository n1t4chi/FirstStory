/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public class DummyGrid2DRenderer implements Grid2DRenderer {
    
    public static final DummyGrid2DRenderer DUMMY_GRID_2D_RENDERER = new DummyGrid2DRenderer();
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {}
}
