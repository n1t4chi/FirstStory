/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author n1t4chi
 */
public interface GraphicObjectRenderer< RenderedObject extends GraphicObject > {
    
    void render( RenderedObject object );
}
