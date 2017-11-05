/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.GraphicObject;

/**
 * @author: n1t4chi
 */
public interface Object3D extends GraphicObject< ObjectTransformations3D, BoundingBox3D, Vertices3D > {

    @Override
    default BoundingBox3D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }

}
