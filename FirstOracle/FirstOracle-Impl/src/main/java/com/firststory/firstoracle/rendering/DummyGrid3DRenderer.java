/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public class DummyGrid3DRenderer implements Grid3DRenderer {
    
    public static final DummyGrid3DRenderer DUMMY_GRID_3D_RENDERER = new DummyGrid3DRenderer();
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {}
}
