/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author n1t4chi
 */
public interface Object2D extends GraphicObject< ObjectTransformations2D, BoundingBox2D, Vertices2D > {
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
}
