/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author: n1t4chi
 */
public interface Object3D {
    
    Texture getTexture();
    
    UVMap getUvMap();
    
    Vertices getVertices();
    
    ObjectTransformations getTransformations();
    
    default BoundingBox getBBO(){
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
}
