/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

/**
 * @author n1t4chi
 */
public interface RenderableScene {
    
    RenderableScene2D getScene2D();
    
    RenderableScene3D getScene3D();
    
    RenderableBackground getBackground();
    
    RenderableOverlay getOverlay();
    
    void dispose();
}
