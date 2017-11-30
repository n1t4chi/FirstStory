/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.rendering.Object3DRenderer;

/**
 * @author n1t4chi
 */
public interface RenderedObjects3D {
    
    default void render( Object3DRenderer renderer ) {
    }
}
