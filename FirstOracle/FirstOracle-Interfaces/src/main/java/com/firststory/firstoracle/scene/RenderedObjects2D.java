/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.rendering.Object2DRenderer;

/**
 * @author n1t4chi
 */
public interface RenderedObjects2D {

    default void render( Object2DRenderer renderer ){}
}
