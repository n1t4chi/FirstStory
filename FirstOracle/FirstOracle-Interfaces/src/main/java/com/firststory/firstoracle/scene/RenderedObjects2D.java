/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.rendering.Object2DRenderer;
import com.firststory.firstoracle.rendering.Terrain2DRenderer;

/**
 * @author n1t4chi
 */
public interface RenderedObjects2D {
    
    void render( Object2DRenderer objectRenderer, Terrain2DRenderer terrainRenderer );
}
