/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.rendering.Object3DRenderer;
import com.firststory.firstoracle.rendering.Terrain3DRenderer;

/**
 * @author n1t4chi
 */
public interface RenderedObjects3D {
    
    void render( Object3DRenderer objectRenderer, Terrain3DRenderer terrainRenderer );
}
